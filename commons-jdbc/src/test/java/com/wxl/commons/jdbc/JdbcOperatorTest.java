package com.wxl.commons.jdbc;

import com.google.common.collect.Table;
import com.wxl.commons.util.PrettyPrinter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/10/16
 * jdbc操作测试
 */
public class JdbcOperatorTest {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/wxl_test?useSSL=false";

    private static final String username = "root";

    private static final String password = "123456";

    private static final String driver = "com.mysql.jdbc.Driver";

    private static JdbcOperator operator;

    @BeforeAll
    public static void start() throws Exception {
        operator = new JdbcOperator(driver, url, username, password, false);
    }

    @AfterAll
    public static void end() throws Exception {
        operator.close();
    }

    @Test
    public void update() throws Exception {
        int count = operator.update("insert into user(name,status) values(?,?),(?,?)", "a", 1, "b", 0);
        System.out.println(count);
    }

    @Test
    public void query() throws Exception {
        Table<Integer, String, Object> table = operator.query("select * from user");
        PrettyPrinter.printTable(table);

        User user = operator.queryOne(User.class, "select * from user where id=?", 1);
        PrettyPrinter.printJson(user);

        List<User> users = operator.query(User.class, "select * from user");
        PrettyPrinter.printJson(users);

        List<Map<String, Object>> list = operator.query(JdbcMapping.map(), "select * from user");
        PrettyPrinter.printJson(list);
    }

    @Test
    public void queryOne() throws Exception {
        Integer id = operator.queryOne(Integer.class, "select id from user limit 1");
        System.out.println(id);

        List<Integer> ids = operator.query(Integer.class, "select id from user");
        System.out.println(ids);

        List<Date> dates = operator.query(Date.class, "select create_time from user");
        System.out.println(dates);

        List<Long> timestamp = operator.query(Long.class, "select create_time from user");
        System.out.println(timestamp);

        Boolean res = operator.queryOne(Boolean.class, "select count(*) from user");
        System.out.println(res);
    }

    @Test
    public void count() throws Exception {
        long count = operator.count("select count(*) from user");
        System.out.println(count);
    }

    @Test
    public void testTransaction() throws Exception {
        operator.startTransaction();
        try {
            operator.update("update user set status=? where id=?", 1, 1);
            operator.update("update user set name=? where id=?", "hehe", 2);
            operator.update("update user set id=? where name=?", 1, "a");
            operator.commit();
        } catch (Exception e) {
            e.printStackTrace();
            operator.rollback();
        }
    }
}
