package com.wxl.commons.jdbc.helper;

import com.wxl.commons.jdbc.JdbcOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Created by wuxingle on 2017/12/11.
 * 随机批量插入数据
 * 没有事务管理
 */
@Slf4j
public class BatchInsertHelper {

    static final int SINGLE_TASK_MAX_GENERATE_DATA = 5000;

    static final int SINGLE_TASK_MAX_HANDLE = 10_000;

    static final int INSERT_BATCH_MAX_LIMIT = 1000;

    private final JdbcOperator jdbcOperator;

    /**
     * 插入的表名
     */
    private final String tableName;

    /**
     * 插入的表字段
     */
    private final List<String> columns;

    /**
     * 运行线程池
     */
    @Nullable
    private final ForkJoinPool forkJoinPool;

    /**
     * 单条线程生成数据大小
     */
    private int singleTaskMaxGenerateData = SINGLE_TASK_MAX_GENERATE_DATA;

    /**
     * 单条线程处理数据大小
     */
    private int singleTaskMaxHandle = SINGLE_TASK_MAX_HANDLE;

    /**
     * 一次性批量插入数据大小
     */
    private int insertBatchMaxLimit = INSERT_BATCH_MAX_LIMIT;


    BatchInsertHelper(JdbcOperator jdbcOperator,
                      String tableName, List<String> columns,
                      @Nullable ForkJoinPool forkJoinPool,
                      Integer singleTaskMaxGenerateData,
                      Integer singleTaskMaxHandle,
                      Integer insertBatchMaxLimit) {
        this.jdbcOperator = jdbcOperator;
        this.tableName = tableName;
        this.columns = columns;
        this.forkJoinPool = forkJoinPool;
        if (singleTaskMaxGenerateData != null) {
            this.singleTaskMaxGenerateData = singleTaskMaxGenerateData;
        }
        if (singleTaskMaxHandle != null) {
            this.singleTaskMaxHandle = singleTaskMaxHandle;
        }
        if (insertBatchMaxLimit != null) {
            this.insertBatchMaxLimit = insertBatchMaxLimit;
        }
    }

    /**
     * 批量插入数据
     *
     * @param count        插入数
     * @param clazz        插入的类型
     * @param rowGenerator key为自增主键，value为行数据
     * @return 插入数
     */
    public <T> long invokeBatchInsert(int count, Class<T> clazz,
                                      Function<Long, T> rowGenerator) {
        return invokeBatchInsert(count, 1L, clazz, rowGenerator);
    }

    /**
     * 批量插入数据
     *
     * @param count        插入数
     * @param startId      起始自增数据id
     * @param clazz        插入的类型
     * @param rowGenerator key为自增主键，value为行数据
     * @return 插入数
     */
    public <T> long invokeBatchInsert(int count, long startId, Class<T> clazz,
                                      Function<Long, T> rowGenerator) {
        // 获取需要入库的字段
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)
                    || Modifier.isFinal(modifiers)
                    || Modifier.isTransient(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            fields.add(field);
        }

        return invokeBatchInsert(count, startId, id -> {
            T target = rowGenerator.apply(id);
            List<Object> row = new ArrayList<>(fields.size());
            for (Field field : fields) {
                Object value = ReflectionUtils.getField(field, target);
                row.add(value);
            }
            return row;
        });
    }

    /**
     * 批量插入数据
     *
     * @param count        插入数
     * @param rowGenerator key为自增主键，value为行数据
     * @return 插入数
     */
    public long invokeBatchInsert(int count, Function<Long, List<Object>> rowGenerator) {
        return invokeBatchInsert(count, 1L, rowGenerator);
    }

    /**
     * 批量插入数据
     *
     * @param count        插入数
     * @param startId      起始自增主键
     * @param rowGenerator key为自增主键，value为行数据
     * @return 插入数
     */
    public long invokeBatchInsert(int count, long startId, Function<Long, List<Object>> rowGenerator) {
        log.debug("batch insert start, use config singleTaskMaxGenerateData={}, singleTaskMaxHandle={}, insertBatchMaxLimit={}",
                singleTaskMaxGenerateData, singleTaskMaxHandle, insertBatchMaxLimit);

        // 返回数据检查
        Function<Long, List<Object>> rowGeneratorWrapper = id -> {
            List<Object> apply = rowGenerator.apply(id);
            if (CollectionUtils.isEmpty(apply) || columns.size() != apply.size()) {
                throw new IllegalStateException("generator row size:" + apply.size()
                        + ", not match columns size:" + columns.size());
            }
            return apply;
        };

        // 如果没有设置pool创建一个
        ForkJoinPool pool = forkJoinPool;
        boolean close = false;
        if (pool == null) {
            pool = new ForkJoinPool();
            close = true;
        }

        try {
            BlockingQueue<List<Object>> queue = new LinkedBlockingQueue<>(count);
            // 数据生成任务
            GenerateTask generateTask = new GenerateTask(rowGeneratorWrapper, queue, startId, count + startId);
            // 数据插入任务
            InsertTask insertTask = new InsertTask(queue, startId, count + startId);

            ForkJoinTask<Long> submit = pool.submit(generateTask);
            // 阻塞直到任务完成
            Long updateNum = pool.invoke(insertTask);

            if (generateTask.isCompletedAbnormally()) {
                throw new RuntimeException(generateTask.getException());
            }
            if (insertTask.isCompletedAbnormally()) {
                throw new RuntimeException(insertTask.getException());
            }

            try {
                // 比较插入和生成的数据是否相等
                Long generateNum = submit.get();
                if (!generateNum.equals(updateNum)) {
                    log.warn("generate data num : {} can not equals insert num:{}", generateNum, updateNum);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException(e);
            }
            return updateNum;

        } finally {
            if (close) {
                pool.shutdown();
            }
        }
    }

    /**
     * 生成批量插入的sql
     */
    private String generateInsertsSQL(int count) {
        StringBuilder sqlsb = new StringBuilder();
        sqlsb.append("insert into ")
                .append(tableName)
                .append(" (");
        Iterator<String> columnIt = columns.iterator();
        while (columnIt.hasNext()) {
            sqlsb.append(columnIt.next());
            if (columnIt.hasNext()) {
                sqlsb.append(",");
            }
        }
        sqlsb.append(") values (");

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < columns.size(); j++) {
                sqlsb.append(j == columns.size() - 1 ? "?),(" : "?,");
            }
        }
        return sqlsb.substring(0, sqlsb.length() - 2);
    }

    /**
     * 数据插入任务
     */
    private class InsertTask extends RecursiveTask<Long> {

        private static final long serialVersionUID = -562286401916407557L;

        private BlockingQueue<List<Object>> queue;

        private long start;

        private long end;

        InsertTask(BlockingQueue<List<Object>> queue, long start, long end) {
            this.queue = queue;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= singleTaskMaxHandle) {
                // 待插入数
                long capacity = end - start;
                // 当前插入数
                long current = 0;
                // 待插入数据
                List<List<Object>> data = new ArrayList<>(insertBatchMaxLimit);
                try {
                    while (current < capacity) {
                        // 限制批量插入大小
                        int batchSize = (int) Math.min(capacity - current, insertBatchMaxLimit);
                        // 获取的数据大小
                        int dataSize = queue.drainTo(data, batchSize);
                        if (dataSize == 0) {
                            Thread.yield();
                            continue;
                        }
                        String sql = generateInsertsSQL(dataSize);
                        Object[] params = data.stream().flatMap(Collection::stream).toArray();
                        int update = jdbcOperator.update(sql, params);
                        if (update != dataSize) {
                            log.error("the update num not equals generate num, except is: {}, but update len is: {}", dataSize, update);
                            throw new IllegalStateException("data length is: " + dataSize + ", but update result is: :" + update);
                        }

                        log.debug("insert `{}` success: {} -> {}", tableName,
                                start + current, start + current + dataSize);
                        data.clear();
                        current += dataSize;
                    }
                    return current;
                } catch (SQLException e) {
                    log.error("insert task execute error:{}, {}", start, end, e);
                    throw new IllegalStateException(e);
                }
            } else {
                long mid = (start + end) >>> 1;
                InsertTask task1 = new InsertTask(queue, start, mid);
                InsertTask task2 = new InsertTask(queue, mid, end);
                task1.fork();
                task2.fork();
                return task1.join() + task2.join();
            }
        }
    }

    /**
     * 数据生成任务
     */
    private class GenerateTask extends RecursiveTask<Long> {

        private static final long serialVersionUID = 8771267383245417077L;

        private Function<Long, List<Object>> generator;

        private BlockingQueue<List<Object>> queue;

        private long start;

        private long end;

        GenerateTask(Function<Long, List<Object>> generator,
                     BlockingQueue<List<Object>> queue,
                     long start, long end) {
            this.generator = generator;
            this.queue = queue;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= singleTaskMaxGenerateData) {
                try {
                    for (long i = start; i < end; i++) {
                        List<Object> data = generator.apply(i);
                        queue.put(data);
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException("generate task was interrupted", e);
                }
                return end - start;
            }
            long mid = (end + start) >>> 1;
            GenerateTask generateTask1 = new GenerateTask(generator, queue, start, mid);
            GenerateTask generateTask2 = new GenerateTask(generator, queue, mid, end);
            generateTask1.fork();
            generateTask2.fork();
            return generateTask1.join() + generateTask2.join();
        }
    }


}

