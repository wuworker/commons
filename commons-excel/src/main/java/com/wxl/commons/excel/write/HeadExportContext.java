package com.wxl.commons.excel.write;

import org.apache.poi.ss.usermodel.*;

/**
 * Create by wuxingle on 2020/11/02
 * 当前head导出上下文
 */
class HeadExportContext extends AbstractExportContext {

    public HeadExportContext(Workbook workbook, Sheet sheet) {
        super(workbook, sheet);
    }

    @Override
    public Font generateFont() {
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
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
