package com.wxl.commons.jdbc;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create by wuxingle on 2020/10/15
 * jdbc工具类
 */
public class JdbcOperator {

    @Getter
    private final String driver;

    @Getter
    private final String url;

    @Getter
    private final String username;

    @Getter
    private final String password;

    //是否自动关闭连接
    @Getter
    private final boolean autoClose;

    //数据库连接
    private final ThreadLocal<Connection> connections = new ThreadLocal<>();

    //当前是否在事务中
    private final ThreadLocal<Boolean> transactionState = new ThreadLocal<>();

    public JdbcOperator(String driver, String url, String username, String password) throws ClassNotFoundException {
        this(driver, url, username, password, false);
    }

    public JdbcOperator(String driver, String url, String username, String password, boolean autoClose) throws ClassNotFoundException {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.autoClose = autoClose;
        Class.forName(driver);
    }

    /**
     * 更新
     */
    public int update(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!ObjectUtils.isEmpty(params)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            return statement.executeUpdate();
        } finally {
            closeAuto(null);
        }
    }

    /**
     * 查询为目标类型
     */
    public <T> List<T> query(Class<T> clazz, String sql, Object... params) throws SQLException {
        return query(clazz, null, sql, params);
    }


    public <T> T queryOne(Class<T> clazz, String sql, Object... params) throws SQLException {
        List<T> list = query(clazz, null, sql, params);
        return limitOne(list);
    }

    /**
     * 查询为目标类型
     */
    public <T> List<T> query(Class<T> clazz, NameMapping nameMapping, String sql, Object... params) throws SQLException {
        JdbcMapping<T> mapping = new AutoClassJdbcMapping<>(clazz, nameMapping);
        return query(mapping, sql, params);
    }


    public <T> T queryOne(Class<T> clazz, NameMapping nameMapping, String sql, Object... params) throws SQLException {
        List<T> list = query(clazz, nameMapping, sql, params);
        return limitOne(list);
    }

    /**
     * 查询为特定类型
     */
    public <T> List<T> query(JdbcMapping<T> mapping, String sql, Object... params) throws SQLException {
        Table<Integer, String, Object> table = query(sql, params);
        Set<Integer> rowIndexes = table.rowKeySet();

        List<T> list = new ArrayList<>(rowIndexes.size());
        for (Integer index : rowIndexes) {
            Map<String, Object> row = table.row(index);
            list.add(mapping.mapping(row));
        }
        return list;
    }


    public <T> T queryOne(JdbcMapping<T> mapping, String sql, Object... params) throws SQLException {
        List<T> list = query(mapping, sql, params);
        return limitOne(list);
    }

    /**
     * 查询
     */
    public ArrayTable<Integer, String, Object> query(String sql, Object... params) throws SQLException {
        Table<Integer, String, Object> table = HashBasedTable.create();

        Connection connection = getConnection();
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int row = 0;
            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    table.put(row, metaData.getColumnName(i), resultSet.getObject(i));
                }
                row++;
            }
        } finally {
            closeAuto(resultSet);
        }
        return ArrayTable.create(table);
    }

    /**
     * 查个数
     */
    public long count(String sql, Object... params) throws SQLException {
        Long res = queryOne(Long.class, sql, params);
        return res == null ? 0 : res;
    }

    /**
     * 开始事务
     */
    public void startTransaction() throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        transactionState.set(true);
    }

    /**
     * 提交
     */
    public void commit() throws SQLException {
        try {
            Connection connection = getConnection();
            //commit出现异常，不设标志位，方便rollback
            connection.commit();
            connection.setAutoCommit(true);
            transactionState.set(false);
        } finally {
            closeAuto(null);
        }
    }

    /**
     * 回滚
     */
    public void rollback() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            //rollback出现异常，退出事务
            connection.rollback();
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            transactionState.set(false);
            closeAuto(null);
        }
    }

    /**
     * 关闭连接
     */
    public void close() throws SQLException {
        Connection connection = connections.get();
        if (connection != null) {
            try {
                connection.close();
            } finally {
                connections.remove();
            }
        }
    }

    /**
     * 关闭连接
     */
    private void closeAuto(ResultSet resultSet) throws SQLException {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } finally {
            if (autoClose && (!Boolean.TRUE.equals(transactionState.get()))) {
                close();
            }
        }
    }

    /**
     * 获取连接
     */
    private Connection getConnection() throws SQLException {
        if (connections.get() == null) {
            Connection connection = DriverManager.getConnection(url, username, password);
            connections.set(connection);
        }
        return connections.get();
    }

    /**
     * 返回1条结果
     */

    private <T> T limitOne(List<T> list) throws SQLException {
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new SQLException("sql result find " + list.size() + ",but expect is one!");
        }
        return list.get(0);
    }
}
