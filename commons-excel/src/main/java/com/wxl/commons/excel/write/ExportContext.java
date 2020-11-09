package com.wxl.commons.excel.write;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Created by wuxingle on 2017/11/27.
 * 当前导出上下文
 */
public interface ExportContext {

    /**
     * 创建字体
     */
    Font generateFont();

    /**
     * 创建cell风格
     */
    CellStyle generateCellStyle();

    /**
     * 新生成一行
     */
    Row generateRow();

    /**
     * 获取行数
     */
    int getRowCount();

    /**
     * 获取当前sheet
     */
    Sheet getSheet();
}


