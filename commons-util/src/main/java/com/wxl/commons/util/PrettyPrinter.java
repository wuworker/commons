package com.wxl.commons.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

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
    public static void printHex(@Nullable byte[] bytes) {
        if (bytes == null) {
            System.out.println(nullToString());
        } else {
            System.out.println(Hex.encodeHexString(bytes));
        }
    }

    /**
     * 打印base64
     */
    public static void printBase64(@Nullable byte[] bytes) {
        if (bytes == null) {
            System.out.println(nullToString());
        } else {
            System.out.println(Base64.getEncoder().encodeToString(bytes));
        }
    }

    /**
     * 打印json
     */
    public static void printJson(@Nullable Object object) {
        if (object == null) {
            System.out.println(nullToString());
        } else {
            System.out.println(GSON.toJson(object));
        }
    }

    /**
     * 打印表格
     */
    public static <T> void printTable(@Nullable List<List<T>> table) {
        if (table == null) {
            System.out.println(nullToString());
            return;
        }

        Table<Integer, Integer, Object> newTable = HashBasedTable.create();
        for (int i = 0; i < table.size(); i++) {
            List<?> row = Optional.ofNullable(table.get(i)).orElse(Collections.emptyList());
            for (int j = 0; j < row.size(); j++) {
                Object cell = row.get(j);
                newTable.put(i + 1, j + 1, cell);
            }
        }

        printTable(newTable);
    }

    /**
     * 打印表格
     */
    public static <R, C, V> void printTable(@Nullable Table<R, C, V> table) {
        if (table == null) {
            System.out.println(nullToString());
            return;
        }

        // 表格每列的最大字符长度
        List<Integer> columnLens = tableColumnLens(table);

        // 打印head
        Set<C> columnKeys = table.columnKeySet();
        List<String> head = new ArrayList<>(columnKeys.size() + 1);
        head.add("");
        head.addAll(columnKeys.stream().map(PrettyPrinter::toString).toList());

        printTableSplit(columnLens);
        printTableRow(head, columnLens);
        printTableSplit(columnLens);

        // 打印数据
        Set<R> rowKeys = table.rowKeySet();
        for (R rowKey : rowKeys) {
            List<String> data = new ArrayList<>();
            data.add(toString(rowKey));
            for (C columnKey : columnKeys) {
                data.add(toString(table.get(rowKey, columnKey)));
            }

            printTableRow(data, columnLens);
        }

        printTableSplit(columnLens);
    }

    /**
     * 返回表格每列的最大显示长度
     */
    private static <R, C, V> List<Integer> tableColumnLens(Table<R, C, V> table) {
        List<Integer> columnLens = new ArrayList<>();
        int rowKeyMaxLen = maxDisplayLength(table.rowKeySet());
        columnLens.add(rowKeyMaxLen);

        for (C columnKey : table.columnKeySet()) {
            Map<R, V> columns = table.column(columnKey);
            int len = Math.max(maxDisplayLength(columns.values()), toString(columnKey).length());
            columnLens.add(len);
        }
        return columnLens;
    }

    /**
     * 打印表格分隔符
     *
     * @param columnLens 每列长度
     */
    private static void printTableSplit(List<Integer> columnLens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnLens.size(); i++) {
            sb.append('+');
            String split = StringUtils.rightPad("-", columnLens.get(i) + 2, '-');
            sb.append(split);
            if (i >= columnLens.size() - 1) {
                sb.append('+');
            }
        }
        System.out.println(sb);
    }

    /**
     * 打印表格数据行
     *
     * @param row        行数据
     * @param columnLens 每列长度
     */
    private static void printTableRow(List<String> row, List<Integer> columnLens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            Integer len = columnLens.get(i);
            sb.append("| ");
            String cell = row.get(i);
            sb.append(StringUtils.rightPad(cell, cell.length() + (len - getDisplayLen(cell))));
            sb.append(" ");
            if (i >= row.size() - 1) {
                sb.append("|");
            }
        }
        System.out.println(sb);
    }

    /**
     * 分栏打印json
     *
     * @param columnLimit 单栏最大列
     * @param objects     打印对象
     */
    public static void printSideJson(Integer columnLimit, Object... objects) {
        Object[] jsons = new Object[objects.length];
        for (int i = 0; i < jsons.length; i++) {
            jsons[i] = GSON.toJson(objects[i]);
        }
        printSideBySide(columnLimit, jsons);
    }

    public static void printSideJson(Object... objects) {
        printSideJson(100, objects);
    }

    /**
     * 分栏打印
     *
     * @param objects 打印对象
     */
    public static void printSideBySide(Object... objects) {
        printSideBySide(100, objects);
    }

    /**
     * 分栏打印
     *
     * @param columnLimit 单栏最大列
     * @param objects     打印对象
     */
    public static void printSideBySide(Integer columnLimit, Object... objects) {
        Assert.isTrue(columnLimit != null && columnLimit > 0, "column limit must > 0");

        List<String[]> sides = new ArrayList<>(objects.length + 1);
        for (Object object : objects) {
            sides.add(toString(object).split(System.lineSeparator()));
        }
        // 最大列数
        int maxColumn = Math.min(columnLimit, maxDisplayLength(sides.stream().flatMap(Arrays::stream)));

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

    public static void printThreadInfo(@Nullable Object obj) {
        printThreadInfo(Thread.currentThread(), obj);
    }

    public static void printThreadInfo(Thread thread, @Nullable Object obj) {
        String info = ThreadUtils.getPrintThreadInfo(thread);
        if (obj != null) {
            info = "[" + info + "]: " + toString(obj);
        }
        System.out.println(info);
    }

    /**
     * 列表中字符串的最大显示长度
     */
    private static int maxDisplayLength(Collection<?> collection) {
        return maxDisplayLength(collection.stream());
    }

    private static int maxDisplayLength(Stream<?> stream) {
        return stream.map(PrettyPrinter::toString)
                .mapToInt(PrettyPrinter::getDisplayLen)
                .max()
                .orElse(0);
    }

    private static String toString(@Nullable Object obj) {
        return obj == null ? nullToString() : obj.toString();
    }

    private static String nullToString() {
        return "null";
    }

    /**
     * 获取显示长度，非ascii字符宽度默认12/7的比例
     */
    private static int getDisplayLen(String str) {
        BigDecimal bigDecimal = BigDecimal.valueOf(0);
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (c < 128) {
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(1));
            } else {
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(12 / 7.0));
            }
        }
        return bigDecimal.intValue();
    }
}

