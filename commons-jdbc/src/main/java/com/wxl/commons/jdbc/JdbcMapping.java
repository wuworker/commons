package com.wxl.commons.jdbc;

import java.util.Map;

/**
 * Create by wuxingle on 2020/10/16
 * 行数据映射为对象
 */
public interface JdbcMapping<T> {

    /**
     * 行数据映射为对象
     *
     * @param data 行数据
     * @return 对象
     */
    T mapping(Map<String, Object> data);

}
