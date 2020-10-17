package com.wxl.commons.util;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by wuxingle on 2017/11/24.
 * 别名工具类
 */
public class AliasUtils {


    /**
     * 蛇形转驼峰
     * a_b -> aB
     */
    public static String snakeToCamel(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        int index;
        while ((index = name.indexOf("_")) != -1) {
            //最后一位
            if (index == name.length() - 1) {
                return name.substring(0, name.length() - 1);
            }
            char c = name.charAt(index + 1);
            name = name.substring(0, index)
                    + Character.toUpperCase(c)
                    + name.substring(index + 2);
        }
        return name;
    }

    /**
     * 蛇形转大驼峰
     * a_b -> AB
     */
    public static String snakeToUpperCamel(String name) {
        return camelToUpper(snakeToCamel(name));
    }

    /**
     * 驼峰转蛇形
     * aB -> a_b
     */
    public static String camelToSnake(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                name = name.substring(0, i)
                        + "_"
                        + Character.toLowerCase(c)
                        + name.substring(i + 1);
                i = i + 1;
            }
        }
        return name;
    }

    /**
     * 大驼峰转蛇形
     * AB -> a_b
     */
    public static String upperCamelToSnake(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        String snake = camelToSnake(name);
        return snake.startsWith("_") ? snake.substring(1) : snake;
    }

    /**
     * 小驼峰转大驼峰
     * aB -> AB
     */
    public static String camelToUpper(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 大驼峰转小驼峰
     * aB -> AB
     */
    public static String camelToLower(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

}

