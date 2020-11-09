package com.wxl.commons.excel.write;

import org.apache.poi.ss.usermodel.*;

/**
 * Create by wuxingle on 2020/11/02
 * 当前body导出上下文
 */
class BodyExportContext extends AbstractExportContext {

    public BodyExportContext(Workbook workbook, Sheet sheet, int index) {
        super(workbook, sheet, index);
    }

    @Override
    public Font generateFont() {
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    @Override
    public CellStyle generateCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
}
