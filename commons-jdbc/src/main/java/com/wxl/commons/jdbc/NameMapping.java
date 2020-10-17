package com.wxl.commons.jdbc;

import com.wxl.commons.util.AliasUtils;

/**
 * Create by wuxingle on 2020/10/16
 * 数据库字段到实体类的映射
 */
public interface NameMapping {

    /**
     * @param name 数据库字段
     * @return 实体类字段
     */
    String mapping(String name);

    default NameMapping andThen(NameMapping nameMapping) {
        return name -> mapping(nameMapping.mapping(name));
    }

    static NameMapping toCamel() {
        return AliasUtils::snakeToCamel;
    }

    static NameMapping toSnake() {
        return AliasUtils::camelToSnake;
    }

    static NameMapping upper() {
        return String::toUpperCase;
    }

    static NameMapping lower() {
        return String::toLowerCase;
    }

}

