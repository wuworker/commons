package com.wxl.commons.jdbc;

import com.wxl.commons.jdbc.helper.BatchInsertBuilder;
import com.wxl.commons.jdbc.helper.BatchInsertHelper;
import com.wxl.commons.util.RandomUtils;
import lombok.Data;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Create by wuxingle on 2020/11/13
 * 批量插入测试
 */
public class BatchInsertHelperTest {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/wxl_test?useSSL=false";

    private static final String username = "root";

    private static final String password = "123456";

    private static final String driver = "com.mysql.jdbc.Driver";

    private static JdbcOperator operator;

    private static BatchInsertHelper batchInsertHelper;

    @BeforeClass
    public static void start() throws Exception {
        operator = new JdbcOperator(driver, url, username, password, false);
        batchInsertHelper = BatchInsertBuilder.builder(operator)
                .setTableName("user")
                .setColumns(Arrays.asList("id", "name", "status", "create_time"))
                .setInsertBatchMaxLimit(100)
                .setSingleTaskMaxGenerateData(50)
                .setSingleTaskMaxHandle(100)
                .build();
    }

    @AfterClass
    public static void end() throws Exception {
        operator.close();
    }

    @Test
    public void test() {
        batchInsertHelper.invokeBatchInsert(1000, 2001,
                id -> Arrays.asList(id, RandomUtils.randomAlphabetic(10), RandomUtils.nextInt(), new Date()));
    }

    @Test
    public void test2() {
        long count = batchInsertHelper.invokeBatchInsert(1000, 3001,
                TestUser.class,
                id -> {
                    TestUser user = new TestUser();
                    user.setId(id.intValue());
                    user.setName(RandomUtils.randomAlphabetic(10));
                    user.setStatus(RandomUtils.nextInt());
                    user.setCreateTime(new Date());
                    return user;
                });
        System.out.println(count);
    }

    @Data
    public static class TestUser implements Serializable {

        private static final long serialVersionUID = 5494294941709489004L;

        private Integer id;

        private String name;

        private Integer status;

        private Date createTime;
    }
}
