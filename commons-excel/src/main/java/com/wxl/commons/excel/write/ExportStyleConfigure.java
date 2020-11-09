package com.wxl.commons.excel.write;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

/**
 * Created by wuxingle on 2017/11/27.
 * excel导出风格设置
 */
public interface ExportStyleConfigure {

    /**
     * 单元格和字体配置
     *
     * @param font
     * @param cellStyle
     */
    void configureStyle(Font font, CellStyle cellStyle);

}

