package com.wxl.commons.jdbc;

import com.google.common.collect.Table;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Create by wuxingle on 2020/10/16
 */
public class JdbcOperatorTest {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/wxl_test?useSSL=false";

    private static final String username = "root";

    private static final String password = "123456";

    private static final String driver = "com.mysql.cj.jdbc.Driver";

    private static JdbcOperator operator;

    @BeforeClass
    public static void start() throws Exception {
        operator = new JdbcOperator(driver, url, username, password, false);
    }

    @AfterClass
    public static void end() throws Exception {
        operator.close();
    }

    @Test
    public void update() {
    }

    @Test
    public void query() throws Exception {
        Table<Integer, String, Object> table = operator.query("select * from user");
        System.out.println(table);
    }
}
