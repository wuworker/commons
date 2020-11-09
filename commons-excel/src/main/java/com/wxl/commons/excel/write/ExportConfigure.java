package com.wxl.commons.excel.write;

import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Map;

/**
 * Created by wuxingle on 2017/11/27.
 * excel导出参数设置
 */
public interface ExportConfigure {

    /**
     * 设置合并的单元格
     */
    default CellRangeAddress[] getMergeRange() {
        return null;
    }

    /**
     * 行高
     * key 第几行
     * valye 高度(磅)
     */
    default Map<Integer, Integer> getRowHeight() {
        return null;
    }

    /**
     * 列宽
     * key 第几列
     * valye 字符数
     */
    default Map<Integer, Integer> getColumnCharNum() {
        return null;
    }

}



