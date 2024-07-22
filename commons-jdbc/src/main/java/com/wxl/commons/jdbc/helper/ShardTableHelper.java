package com.wxl.commons.jdbc.helper;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.wxl.commons.jdbc.JdbcMapping;
import com.wxl.commons.jdbc.JdbcOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by wuxingle on 2018/02/08
 * 分表创建、查询类
 */
@Slf4j
public class ShardTableHelper {

    private final JdbcOperator operator;

    public ShardTableHelper(JdbcOperator operator) {
        this.operator = operator;
    }

    /**
     * 以模版表为基础创建分表
     *
     * @param templateTableName 基础表
     * @param tableNames        分表表名
     */
    public void createTablesLike(String templateTableName, Collection<String> tableNames) throws SQLException {
        for (String tableName : tableNames) {
            operator.update("create table " + tableName + " like " + templateTableName);
            log.debug("create table `{}` success", tableName);
        }
    }

    /**
     * 删除表
     */
    public void dropTables(String... tableNames) throws SQLException {
        dropTables(Arrays.asList(tableNames));
    }

    public void dropTables(Collection<String> tableNames) throws SQLException {
        for (String name : tableNames) {
            operator.update("drop table " + name);
            log.debug("drop table `{}` success", name);
        }
    }

    /**
     * 清空所有数据
     *
     * @return key为表名，value为清理的个数
     */
    public Map<String, Integer> clearTables(String... tableNames) throws SQLException {
        return clearTables(Arrays.asList(tableNames));
    }

    public Map<String, Integer> clearTables(Collection<String> tableNames) throws SQLException {
        if (CollectionUtils.isEmpty(tableNames)) {
            return Collections.emptyMap();
        }
        Map<String, Integer> result = new LinkedHashMap<>(tableNames.size());
        try {
            operator.startTransaction();
            for (String table : tableNames) {
                int count = operator.update("delete from " + table);
                log.debug("clear table `{}` success: {}", table, count);

                result.put(table, count);
            }
            operator.commit();
        } catch (Exception e) {
            operator.rollback();
            throw e;
        }

        return result;
    }

    /**
     * 对所有分表进行更新
     */
    public Map<String, Integer> update(String baseTable, Collection<String> tableNames,
                                       String sql, Object... params) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        try {
            operator.startTransaction();
            for (String table : tableNames) {
                String exeSql = sql.replaceAll(baseTable, table);
                int count = operator.update(exeSql, params);

                log.debug("update table `{}` success: {}", table, count);
                map.put(table, count);
            }
            operator.commit();
            return map;
        } catch (Exception e) {
            operator.rollback();
            throw e;
        }
    }


    /**
     * 从所有分表中查询
     *
     * @param clazz      结果类型
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    public <T> Map<String, T> queryOne(Class<T> clazz,
                                       String baseTable,
                                       Collection<String> tableNames,
                                       String sql, Object... params) throws SQLException {
        return doQuery((s, p) -> operator.queryOne(clazz, s, p), baseTable, tableNames, sql, params);
    }

    /**
     * 从所有分表中查询
     *
     * @param clazz      结果类型
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    public <T> Map<String, List<T>> query(Class<T> clazz,
                                          String baseTable,
                                          Collection<String> tableNames,
                                          String sql, Object... params) throws SQLException {
        return doQuery((s, p) -> operator.query(clazz, s, p), baseTable, tableNames, sql, params);
    }

    /**
     * 从所有分表中查询
     *
     * @param mapping    数据映射方式
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    public <T> Map<String, T> queryOne(JdbcMapping<T> mapping,
                                       String baseTable,
                                       Collection<String> tableNames,
                                       String sql, Object... params) throws SQLException {
        return doQuery((s, p) -> operator.queryOne(mapping, s, p), baseTable, tableNames, sql, params);
    }

    /**
     * 从所有分表中查询
     *
     * @param mapping    数据映射方式
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    public <T> Map<String, List<T>> query(JdbcMapping<T> mapping,
                                          String baseTable,
                                          Collection<String> tableNames,
                                          String sql, Object... params) throws SQLException {
        return doQuery((s, p) -> operator.query(mapping, s, p), baseTable, tableNames, sql, params);
    }


    /**
     * 从所有分表中查询
     *
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    public <T> Map<String, ArrayTable<Integer, String, Object>> query(String baseTable,
                                                                      Collection<String> tableNames,
                                                                      String sql, Object... params) throws SQLException {
        return doQuery(operator::query, baseTable, tableNames, sql, params);
    }

    /**
     * 查询操作
     */
    private interface QueryOperator<T> {

        T query(String sql, Object... params) throws SQLException;
    }

    /**
     * 执行查询
     *
     * @param op         查询操作
     * @param baseTable  基础表名
     * @param tableNames 分表表名
     * @param sql        sql
     * @param params     参数
     * @return 分表key表名，value表数据
     */
    private <T> Map<String, T> doQuery(QueryOperator<T> op,
                                       String baseTable,
                                       Collection<String> tableNames,
                                       String sql, Object... params) throws SQLException {
        Map<String, T> map = new LinkedHashMap<>();
        for (String table : tableNames) {
            String exeSql = sql.replaceAll(baseTable, table);
            T result = op.query(exeSql, params);

            map.put(table, result);
        }
        return map;
    }

    /**
     * 分表策略接口
     */
    public interface ShardStrategy {
        /**
         * @param rowData 原始表的行数据
         * @return 表名
         */
        String getTableName(Map<String, Object> rowData);
    }

    /**
     * 把原来表中的数据分到分表中去
     *
     * @param onceCount 每次批量操作大小
     */
    @SuppressWarnings("unchecked")
    public void shardTableData(String tableName, int onceCount, ShardStrategy shardStrategy) throws SQLException {
        try {
            operator.startTransaction();
            long all = operator.count("select count(*) from " + tableName);
            log.debug("shard table all data count: {}", all);
            if (all == 0) {
                return;
            }

            // 新插入的所有数据大小
            int insertAllCount = 0;
            // 分页起始行数
            int start = 0;
            do {
                Table<Integer, String, Object> originTable = operator.query("select * from " + tableName + " limit ?,?",
                        start, onceCount);
                log.debug("shard table start, index: {} to {}", start, start + onceCount);

                //数据分类
                //分表数据,key表名, value表数据
                Map<String, List<Map<String, Object>>> shardDatas = new HashMap<>();
                for (Map<String, Object> row : originTable.rowMap().values()) {
                    String shardTableName = shardStrategy.getTableName(row);
                    List<Map<String, Object>> shardRow = shardDatas.computeIfAbsent(shardTableName, (k) -> new ArrayList<>());
                    shardRow.add(row);
                }

                //生成sql
                StringBuilder[] sqls = new StringBuilder[shardDatas.size()];
                String[] tableNames = new String[shardDatas.size()];
                List<Object>[] params = new ArrayList[shardDatas.size()];
                int i = 0;
                for (Map.Entry<String, List<Map<String, Object>>> shardData : shardDatas.entrySet()) {
                    String table = shardData.getKey();
                    List<Map<String, Object>> rs = shardData.getValue();
                    sqls[i] = new StringBuilder("insert into " + table + " values ");
                    params[i] = new ArrayList<>();
                    tableNames[i] = table;
                    for (Map<String, Object> row : rs) {
                        sqls[i].append("(");
                        for (Map.Entry<String, Object> column : row.entrySet()) {
                            params[i].add(column.getValue());
                            sqls[i].append("?,");
                        }
                        sqls[i] = sqls[i].replace(sqls[i].length() - 1, sqls[i].length(), "),");
                    }
                    sqls[i] = sqls[i].delete(sqls[i].length() - 1, sqls[i].length());
                    i++;
                }

                //执行sql
                for (i = 0; i < shardDatas.size(); i++) {
                    int count = operator.update(sqls[i].toString(), params[i].toArray(new Object[0]));

                    log.debug("shard table `{}`, count: {}", tableNames[i], count);
                    insertAllCount += count;
                }
                log.debug("shard table end, index: {} to {}", start, start + onceCount);

                start += onceCount;
            } while (start < all);

            operator.commit();
            log.debug("shard table data end, all insert count: {}", insertAllCount);
        } catch (Exception e) {
            operator.rollback();
            throw e;
        }
    }


}





