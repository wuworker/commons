package com.wxl.commons.jdbc.helper;

import com.wxl.commons.jdbc.JdbcOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Create by wuxingle on 2020/11/13
 * 批量插入工具构造
 */
public class BatchInsertBuilder {

    private JdbcOperator jdbcOperator;

    /**
     * 插入的表名
     */
    private String tableName;

    /**
     * 插入的表字段
     */
    private List<String> columns;

    /**
     * 运行线程池
     */
    private ForkJoinPool forkJoinPool;

    /**
     * 单条线程生成数据大小
     */
    private Integer singleTaskMaxGenerateData;

    /**
     * 单条线程处理数据大小
     */
    private Integer singleTaskMaxHandle;

    /**
     * 一次性批量插入数据大小
     */

    private Integer insertBatchMaxLimit;

    private BatchInsertBuilder() {
    }

    public static BatchInsertBuilder builder(JdbcOperator jdbcOperator) {
        BatchInsertBuilder batchInsertBuilder = new BatchInsertBuilder();
        batchInsertBuilder.jdbcOperator = jdbcOperator;
        return batchInsertBuilder;
    }

    public BatchInsertBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public BatchInsertBuilder setColumns(List<String> columns) {
        this.columns = Collections.unmodifiableList(columns);
        return this;
    }

    public BatchInsertBuilder setSingleTaskMaxGenerateData(int singleTaskMaxGenerateData) {
        this.singleTaskMaxGenerateData = singleTaskMaxGenerateData;
        return this;
    }

    public BatchInsertBuilder setSingleTaskMaxHandle(int singleTaskMaxHandle) {
        this.singleTaskMaxHandle = singleTaskMaxHandle;
        return this;
    }

    public BatchInsertBuilder setInsertBatchMaxLimit(int insertBatchMaxLimit) {
        this.insertBatchMaxLimit = insertBatchMaxLimit;
        return this;
    }

    public BatchInsertBuilder setPool(ForkJoinPool pool) {
        this.forkJoinPool = pool;
        return this;
    }

    public BatchInsertHelper build() {
        if (jdbcOperator == null
                || StringUtils.isBlank(tableName)
                || CollectionUtils.isEmpty(columns)
                || (singleTaskMaxGenerateData != null && singleTaskMaxGenerateData <= 0)
                || (singleTaskMaxHandle != null && singleTaskMaxHandle <= 0)
                || (insertBatchMaxLimit != null && insertBatchMaxLimit <= 0)) {
            throw new IllegalArgumentException("absent argument!");
        }
        return new BatchInsertHelper(jdbcOperator,
                tableName,
                columns,
                forkJoinPool,
                singleTaskMaxGenerateData,
                singleTaskMaxHandle,
                insertBatchMaxLimit);
    }

}
