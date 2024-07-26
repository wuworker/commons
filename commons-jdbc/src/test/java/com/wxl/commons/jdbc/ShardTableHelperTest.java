package com.wxl.commons.jdbc;

import com.google.common.collect.ImmutableList;
import com.wxl.commons.jdbc.helper.ShardTableHelper;
import com.wxl.commons.util.PrettyPrinter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/11/12
 * 分表测试
 */
public class ShardTableHelperTest {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/wxl_test?useSSL=false";

    private static final String username = "root";

    private static final String password = "123456";

    private static final String driver = "com.mysql.jdbc.Driver";

    private static JdbcOperator operator;

    private static ShardTableHelper shardTable;

    @BeforeAll
    public static void start() throws Exception {
        operator = new JdbcOperator(driver, url, username, password, false);
        shardTable = new ShardTableHelper(operator);
    }

    @AfterAll
    public static void end() throws Exception {
        operator.close();
    }

    @Test
    public void createTablesLike() throws Exception {
        shardTable.createTablesLike("user",
                ImmutableList.of("user_1", "user_2", "user_3"));
    }

    @Test
    public void dropTables() throws Exception {
        shardTable.dropTables("user_1", "user_2", "user_3");
    }

    @Test
    public void clearTables() throws Exception {
        shardTable.clearTables("user_1", "user_2", "user_3");
    }

    @Test
    public void update() throws Exception {
        Map<String, Integer> res = shardTable.update("user",
                ImmutableList.of("user_1", "user_2", "user_3"),
                "update user set status=?", 0);
        PrettyPrinter.printJson(res);
    }

    @Test
    public void query() throws Exception {
        Map<String, List<User>> result = shardTable.query(User.class, "user",
                ImmutableList.of("user_1", "user_2", "user_3"),
                "select * from user");
        PrettyPrinter.printJson(result);
    }

    @Test
    public void queryOne() throws Exception {
        Map<String, String> result = shardTable.queryOne(String.class, "user",
                ImmutableList.of("user_1", "user_2", "user_3"),
                "select name from user limit 1");
        PrettyPrinter.printJson(result);
    }

    @Test
    public void shardTableData() throws Exception {
        shardTable.shardTableData("user", 5, row -> {
            Number id = (Number) row.get("id");
            return "user_" + (id.intValue() % 3 + 1);
        });
    }
}