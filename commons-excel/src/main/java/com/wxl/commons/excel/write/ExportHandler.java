package com.wxl.commons.excel.write;

import java.util.List;

/**
 * Created by wuxingle on 2017/11/27.
 * excel导出处理
 */
public interface ExportHandler<T> {

    /**
     * 导出数据
     */
    void exportData(List<T> list, ExportContext context);

    /**
     * 导出标题
     */
    default void exportHeader(ExportContext context) {

    }
}

