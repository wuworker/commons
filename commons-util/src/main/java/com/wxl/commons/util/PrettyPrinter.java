package com.wxl.commons.util;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/10/12
 * 打印相关
 */
public class PrettyPrinter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private PrettyPrinter() {
    }

    /**
     * 16进制打印
     */
    public static void printHex(byte[] bytes) {
        System.out.println(Hex.encodeHexString(bytes));
    }

    /**
     * 打印base64
     */
    public static void printBase64(byte[] bytes) {
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * 打印json
     */
    public static void printJson(Object object) {
        System.out.println(GSON.toJson(object));
    }

    /**
     * 打印表格
     *
     * @param table
     */
    public static void printTable(Table<?, ?, ?> table, int cellWidthLimit) {
        Set<?> columnKeys = table.columnKeySet();

        // 打印head
        List<String> head = new ArrayList<>(columnKeys.size() + 1);
        head.add("");
        head.addAll(columnKeys.stream()
                .map(obj -> Objects.toString(obj, "null"))
                .collect(Collectors.toList()));

        printRow(head, cellWidthLimit);

        // 打印数据
        Set<?> rowKeys = table.rowKeySet();
        for (Object rowKey : rowKeys) {
            List<String> data = new ArrayList<>();
            data.add(Objects.toString(rowKey, "null"));
            for (Object columnKey : columnKeys) {
                data.add(Objects.toString(table.get(rowKey, columnKey), "null"));
            }

            printRow(data, cellWidthLimit);
        }
    }


    public static void printRow(List<String> row, int cellWidthLimit) {
        List<List<String>> newRow = row.stream()
                .map(str -> clipByLength(str, cellWidthLimit))
                .collect(Collectors.toList());

        int maxRow = newRow.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < newRow.size(); j++) {
                String line = "";
                if (i < newRow.get(j).size()) {
                    line = newRow.get(j).get(i);
                }
                line = StringUtils.rightPad(line, cellWidthLimit);
                System.out.print(line);

                if (j < newRow.size() - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }
        String splitLine = StringUtils.rightPad("-", cellWidthLimit * newRow.size(), '-');
        System.out.println(splitLine);
    }

    /**
     * 分栏打印json
     *
     * @param columnLimit 单栏最大列
     * @param objects     打印对象
     */
    public static void printSideJson(Integer columnLimit, Object obj, Object... objects) {
        Object[] jsons = new Object[objects.length];
        for (int i = 0; i < jsons.length; i++) {
            jsons[i] = GSON.toJson(objects[i]);
        }
        printSideBySide(columnLimit, GSON.toJson(obj), jsons);
    }

    public static void printSideJson(Object obj, Object... objects) {
        printSideJson(100, obj, objects);
    }

    /**
     * 分栏打印
     *
     * @param objects 打印对象
     */
    public static void printSideBySide(Object obj, Object... objects) {
        printSideBySide(100, obj, objects);
    }

    /**
     * 分栏打印
     *
     * @param columnLimit 单栏最大列
     * @param objects     打印对象
     */
    public static void printSideBySide(Integer columnLimit, Object obj, Object... objects) {
        Assert.isTrue(columnLimit != null && columnLimit > 0, "column limit must > 0");

        List<String[]> sides = new ArrayList<>(objects.length + 1);
        sides.add(Objects.toString(obj, "null").split(System.lineSeparator()));
        for (Object object : objects) {
            sides.add(Objects.toString(object, "null").split(System.lineSeparator()));
        }
        // 最大列数
        int maxColumn = Math.min(columnLimit, sides.stream()
                .flatMap(Arrays::stream)
                .mapToInt(String::length)
                .max()
                .orElse(0));

        // 过长的行修剪
        List<List<String>> newSides = clipByLength(sides, maxColumn);

        // 最大行数
        int maxLines = newSides.stream().mapToInt(List::size).max().orElse(0);

        for (int i = 0; i < maxLines; i++) {
            for (int j = 0; j < newSides.size(); j++) {
                String line = "";
                List<String> side = newSides.get(j);
                if (i < side.size()) {
                    line = side.get(i);
                }
                line = StringUtils.rightPad(line, maxColumn);
                System.out.print(line);

                if (j < newSides.size() - 1) {
                    System.out.print("\t|\t");
                }
            }

            System.out.println();
        }
    }

    /**
     * 剪切超过最大长度的字符串
     */
    private static List<List<String>> clipByLength(List<String[]> sides, int maxLen) {
        List<List<String>> newSides = new ArrayList<>();
        for (String[] lines : sides) {
            List<String> list = new ArrayList<>();
            for (String line : lines) {
                list.addAll(clipByLength(line, maxLen));
            }
            newSides.add(list);
        }
        return newSides;
    }

    /**
     * 剪切超过最大长度的字符串
     */
    private static List<String> clipByLength(String str, int maxLen) {
        List<String> list = new ArrayList<>();
        while (str.length() > maxLen) {
            list.add(str.substring(0, maxLen));
            str = str.substring(maxLen);
        }
        list.add(str);
        return list;
    }

    /**
     * 打印包含线程信息
     */
    public static void printThreadInfo() {
        printThreadInfo(Thread.currentThread());
    }

    public static void printThreadInfo(Thread thread) {
        printThreadInfo(thread, null);
    }

    public static void printThreadInfo(Object obj) {
        printThreadInfo(Thread.currentThread(), obj);
    }

    public static void printThreadInfo(Thread thread, @Nullable Object obj) {
        String info = "thread id:" + thread.getId()
                + ", name:" + thread.getName()
                + ", group:" + thread.getThreadGroup().getName()
                + ", daemon:" + thread.isDaemon()
                + ", priority:" + thread.getPriority()
                + ", state:" + thread.getState().name();
        if (obj != null) {
            info = "[" + info + "]: " + obj;
        }
        System.out.println(info);
    }
}
