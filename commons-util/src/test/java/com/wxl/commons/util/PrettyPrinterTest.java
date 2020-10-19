package com.wxl.commons.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/10/12
 */
public class PrettyPrinterTest {

    @Test
    public void printThread() {
        PrettyPrinter.printThreadInfo("123");
    }

    @Test
    public void printSideBySide() {
        String s1 = "11111\n22222\n33333";
        String s2 = "11111\n44444\n555555555\n77777\n888888";
        PrettyPrinter.printSideBySide(5, s1, s2, "333");
    }

    @Test
    public void printJson() {
        Map<String, Object> m1 = ImmutableMap.of("name", "xiaoming",
                "age", 20,
                "subject", ImmutableList.of("yuwen", "yingyu"));
        Map<String, Object> m2 = ImmutableMap.of("name", "xiaohong",
                "age", 18,
                "subject", ImmutableList.of("yuwen", "yingyu", "shuxue"));
        Map<String, Object> m3 = ImmutableMap.of("name", "哈哈哈哈哈哈哈哈哈哈哈",
                "age", 22,
                "subject", ImmutableList.of("zhengzhi", "lishi", "shuxue", "dili"));

        PrettyPrinter.printSideJson(20, m1, m2, m3);
    }

    @Test
    public void printTable() {
        HashBasedTable<String, String, String> table = HashBasedTable.create();
        table.put("1", "name", "nice");
        table.put("1", "age", "12");
        table.put("1", "subj", "1111111");
        table.put("1", "country", "china");

        table.put("2", "name", "gg");
        table.put("2", "age", "18");
        table.put("2", "subj", "2222222222");
        table.put("2", "country", "eng");

        table.put("3", "name", "hehe");
        table.put("3", "age", "20");
        table.put("3", "subject", "3333333333");
        table.put("3", "country", "jjjj");

        PrettyPrinter.printTable(table);

        List<List<String>> table2 = ImmutableList.of(
                ImmutableList.of("aaaaa", "bbb", "cccccc"),
                ImmutableList.of("ddddd", "eeeee", "fffffffffffff"),
                ImmutableList.of("ggggggg", "哈哈哈", "iiiiiii")
        );

        PrettyPrinter.printTable(table2);
    }


}