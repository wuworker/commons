package com.wxl.commons.excel.write;

import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.util.*;

/**
 * Create by wuxingle on 2020/11/02
 * 默认数据导出
 */
public class DefaultExportHandler<T> implements ExportHandler<T> {

    private final String[] titles;

    @Setter
    private ExportStyleConfigure headStyleConfigure;

    @Setter
    private ExportStyleConfigure bodyStyleConfigure;

    public DefaultExportHandler() {
        this(null);
    }

    public DefaultExportHandler(String[] titles) {
        this.titles = titles;
    }

    public DefaultExportHandler(String[] titles,
                                ExportStyleConfigure headStyleConfigure,
                                ExportStyleConfigure bodyStyleConfigure) {
        this.titles = titles;
        this.headStyleConfigure = headStyleConfigure;
        this.bodyStyleConfigure = bodyStyleConfigure;
    }

    @Override
    public void exportData(List<T> list, ExportContext context) {
        // 数据样式
        CellStyle bodyStyle = context.generateCellStyle();
        Font bodyFont = context.generateFont();
        bodyStyle.setFont(bodyFont);
        if (bodyStyleConfigure != null) {
            bodyStyleConfigure.configureStyle(bodyFont, bodyStyle);
        }

        // 设置数据
        for (T data : list) {
            Row row = context.generateRow();
            List<Object> values = getExportData(data);
            for (int i = 0, len = values.size(); i < len; i++) {
                Cell bodyCell = row.createCell(i);
                bodyCell.setCellStyle(bodyStyle);
                Object val = values.get(i);
                setCellValue(bodyCell, val);

                //前后留10字符
                setSafeColumnWidth(context.getSheet(), i, val.toString().getBytes().length + 10);
            }
        }
    }

    @Override
    public void exportHeader(ExportContext context) {
        if (!ObjectUtils.isEmpty(titles)) {
            // 标题样式
            CellStyle headStyle = context.generateCellStyle();
            Font headFont = context.generateFont();
            headStyle.setFont(headFont);
            if (headStyleConfigure != null) {
                headStyleConfigure.configureStyle(headFont, headStyle);
            }

            Row row = context.generateRow();

            // 设置标题
            for (int i = 0; i < titles.length; i++) {
                Cell cell = row.createCell(i);
                setCellValue(cell, titles[i]);
                cell.setCellStyle(headStyle);
            }
        }
    }

    /**
     * 导出数据转换
     */
    protected List<Object> getExportData(T data) {
        List<Object> value = new ArrayList<>();
        if (data == null) {
            return value;
        }
        if (data instanceof Map<?, ?> map) {
            for (Object key : map.keySet()) {
                Object obj = map.get(key);
                value.add(obj == null ? "" : obj);
            }
        } else if (data instanceof Iterable<?> iterable) {
            for (Object obj : iterable) {
                value.add(obj == null ? "" : obj);
            }
        } else if (data instanceof Enumeration<?> it) {
            while (it.hasMoreElements()) {
                Object obj = it.nextElement();
                value.add(obj == null ? "" : obj);
            }
        } else if (isJavaType(data)) {
            value.add(data);
        } else {
            ReflectionUtils.doWithFields(data.getClass(), f -> {
                f.setAccessible(true);
                Object val = ReflectionUtils.getField(f, data);
                value.add(val == null ? "" : val);
            });
        }
        return value;
    }


    /**
     * 设置单元格的值
     */
    private void setCellValue(Cell cell, @Nullable Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((boolean) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 设置列宽
     * 如果比原来的小不设置
     */
    private boolean setSafeColumnWidth(Sheet sheet, int index, int charNum) {
        int oldLen = sheet.getColumnWidth(index);
        if (charNum > 30) {
            charNum = 30;
        }
        if (oldLen < charNum * 256) {
            ExcelExportUtils.setColumnWidth(sheet, index, charNum);
            return true;
        }
        return false;
    }

    /**
     * 基本类型
     */
    private static boolean isJavaType(Object obj) {
        return obj.getClass().getName().startsWith("java");
    }


}
