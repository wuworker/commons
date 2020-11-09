package com.wxl.commons.excel.write;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create by wuxingle on 2020/11/02
 * 当前导出上下文
 */
public abstract class AbstractExportContext implements ExportContext {

    protected final Workbook workbook;

    protected final Sheet sheet;

    private int rowIndex;

    public AbstractExportContext(Workbook workbook, Sheet sheet) {
        this(workbook, sheet, 0);
    }

    public AbstractExportContext(Workbook workbook, Sheet sheet, int index) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.rowIndex = index;
    }

    @Override
    public Row generateRow() {
        return sheet.createRow(rowIndex++);
    }

    @Override
    public int getRowCount() {
        return rowIndex;
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }
}
