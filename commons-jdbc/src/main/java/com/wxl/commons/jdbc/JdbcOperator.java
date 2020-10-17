package com.wxl.commons.jdbc;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;

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
        this(driver, url, username, password, true);
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
     * 查询为对象
     */
    public <T> List<T> query(Class<T> clazz, String sql, Object... params) throws SQLException {
        return query(clazz, NameMapping.toCamel(), sql, params);
    }

    @Nullable
    public <T> T queryOne(Class<T> clazz, String sql, Object... params) throws SQLException {
        List<T> list = query(clazz, NameMapping.toCamel(), sql, params);
        return limitOne(list);
    }

    /**
     * 查询为对象
     */
    public <T> List<T> query(Class<T> clazz, NameMapping nameMapping, String sql, Object... params) throws SQLException {
        JdbcMapping<T> mapping = data -> {
            T bean = BeanUtils.instantiateClass(clazz);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldName = nameMapping.mapping(entry.getKey());
                Field field = ReflectionUtils.findField(clazz, fieldName);
                if (field != null) {
                    setFieldValue(field, bean, entry.getValue());
                }
            }
            return bean;
        };
        return query(mapping, sql, params);
    }

    @Nullable
    public <T> T queryOne(Class<T> clazz, NameMapping nameMapping, String sql, Object... params) throws SQLException {
        List<T> list = query(clazz, nameMapping, sql, params);
        return limitOne(list);
    }

    /**
     * 查询为对象
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

    @Nullable
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
        Table<Integer, String, Object> table = query(sql, params);
        Map<String, Object> row = table.row(0);
        if (row.isEmpty()) {
            throw new SQLException("can not get count result from sql:" + sql);
        }
        if (row.size() > 1) {
            throw new SQLException("get more one result from sql:" + sql);
        }
        Number count = (Number) row.values().iterator().next();
        return NumberUtils.convertNumberToTargetClass(count, Long.class);
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
    private void closeAuto(@Nullable ResultSet resultSet) throws SQLException {
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
    @Nullable
    private <T> T limitOne(List<T> list) throws SQLException {
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new SQLException("sql result find " + list.size() + ",but expect is one!");
        }
        return list.get(0);
    }

    /**
     * 设置value
     */
    @SuppressWarnings("unchecked")
    private void setFieldValue(Field field, Object target, @Nullable Object value) {
        field.setAccessible(true);
        if (value == null) {
            ReflectionUtils.setField(field, target, null);
            return;
        }

        Class<?> fieldClass = field.getType();
        // value类型和目标类型不一致，尝试转换value类型
        if (!fieldClass.isInstance(value)) {
            // Date和Long转换
            if ((value instanceof Date) && fieldClass == Long.class) {
                value = ((Date) value).getTime();
            }
            // 数值转换
            else if (Number.class.isAssignableFrom(fieldClass) && (value instanceof Number)) {
                value = NumberUtils.convertNumberToTargetClass(
                        (Number) value, (Class<? extends Number>) fieldClass);
            } else {
                throw new IllegalStateException("class cast error by:"
                        + value.getClass() + " to " + fieldClass);
            }
        }

        ReflectionUtils.setField(field, target, value);
    }
}