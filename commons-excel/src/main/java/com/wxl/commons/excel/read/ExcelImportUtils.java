package com.wxl.commons.excel.read;

import com.wxl.commons.util.convert.Getters;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wuxingle on 2017/10/21 0021.
 * excel导入工具类
 */
public class ExcelImportUtils {

    /**
     * 获取workbook
     */
    public static Workbook create(InputStream in) throws IOException {
        try {
            return WorkbookFactory.create(in);
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException("create excel workbook error", e);
        }
    }

    /**
     * excel导入成list类型
     */
    public static List<List<String>> readAndClose(InputStream in) throws IOException {
        return readAndClose(in, 0, 0);
    }

    public static List<List<String>> readAndClose(InputStream in, int rowStart) throws IOException {
        return readAndClose(in, 0, rowStart);
    }

    public static List<List<String>> readAndClose(InputStream in, int sheetIndex, int rowStart) throws IOException {
        return readAndClose(in, sheetIndex, rowStart, (map) -> new ArrayList<>(map.values()));
    }

    public static List<List<String>> read(Workbook workbook, int sheetIndex) {
        return read(workbook, sheetIndex, 0);
    }

    public static List<List<String>> read(Workbook workbook, int sheetIndex, int rowStart) {
        return read(workbook, sheetIndex, rowStart, (map) -> new ArrayList<>(map.values()));
    }

    /**
     * excel导入成java类
     * 默认第0个表
     *
     * @param clazz 对应的类
     */
    public static <T> List<T> readAndClose(InputStream in, Class<T> clazz, int rowStart) throws IOException {
        return readAndClose(in, clazz, 0, rowStart);
    }

    public static <T> List<T> readAndClose(InputStream in, Class<T> clazz, int sheetIndex, int rowStart) throws IOException {
        try {
            Workbook workbook = create(in);
            return read(workbook, clazz, sheetIndex, rowStart);
        } finally {
            close(in);
        }
    }

    public static <T> List<T> read(Workbook workbook, Class<T> clazz, int sheetIndex, int rowStart) {
        return read(workbook, sheetIndex, rowStart, (map) -> {
            try {
                List<String> list = new ArrayList<>(map.values());
                T t = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length && i < list.size(); i++) {
                    setFieldValue(t, fields[i], list.get(i));
                }
                return t;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("can not access default construct", e);
            } catch (InstantiationException e) {
                throw new IllegalStateException("create default construct error", e);
            }
        });
    }

    /**
     * excel导入
     *
     * @param in         输入流
     * @param sheetIndex 表索引，小于0则导入所有表
     * @param rowStart   开始读取的行号，从0开始
     * @param rowHandler 行处理，返回行处理后结果
     * @param <T>        行处理结果
     */
    public static <T> List<T> readAndClose(InputStream in, int sheetIndex, int rowStart,
                                           ImportRowHandler<T> rowHandler) throws IOException {
        try {
            Workbook workbook = create(in);
            return read(workbook, sheetIndex, rowStart, rowHandler);
        } finally {
            close(in);
        }
    }

    public static <T> List<T> read(Workbook workbook, int sheetIndex, int rowStart, ImportRowHandler<T> rowHandler) {
        Assert.isTrue(sheetIndex >= 0, "sheetIndex must >=0");
        Assert.isTrue(rowStart >= 0, "rowStart must >=0");

        List<T> result = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) {
            throw new IllegalStateException("can not find sheet by sheetIndex:" + sheetIndex);
        }

        for (int i = rowStart, len = sheet.getLastRowNum() - sheet.getFirstRowNum(); i <= len; i++) {
            Map<Integer, String> cells = new LinkedHashMap<>();
            for (Cell cell : sheet.getRow(i)) {
                cells.put(cell.getColumnIndex(), getCellString(cell));
            }
            result.add(rowHandler.doWithRow(cells));
        }

        return result;
    }


    /**
     * 关闭输入流
     */
    public static void close(InputStream in) throws IOException {
        in.close();
    }

    /**
     * 设置属性
     */
    private static void setFieldValue(Object target, Field field, @Nullable String cellValue) {
        Class<?> clazz = field.getType();

        Object newVal;
        if (cellValue == null) {
            newVal = clazz == String.class ? "" : null;
        } else {
            newVal = Getters.of(cellValue).get(clazz);
        }

        field.setAccessible(true);
        ReflectionUtils.setField(field, target, newVal);
    }

    /**
     * 获取cell的值
     */
    @Nullable
    private static String getCellString(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                return null;
            case NUMERIC:
                double num = cell.getNumericCellValue();
                return new BigDecimal(num).toString();
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                throw new IllegalStateException("read cell value error:" + cell.getAddress().toString());
            default:
                return cell.toString();
        }
    }


}
