package com.wxl.commons.jdbc;

import java.util.ArrayList;
import java.util.List;
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


    static JdbcMapping<Map<String, Object>> map() {
        return data -> data;
    }

    static JdbcMapping<List<Object>> list() {
        return data -> new ArrayList<>(data.values());
    }
}
