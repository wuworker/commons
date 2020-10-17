package com.wxl.commons.util;

import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Create by wuxingle on 2020/10/15
 * 用与把value转为目标值
 */
public class Getters {

    private final Object value;

    private Getters(Object value) {
        this.value = value;
    }

    public static Getters of(Object value) {
        return new Getters(value);
    }

    /**
     * 已知目标类型下获取
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) value;
    }

    /**
     * 不为目标类型返回空
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 不是目标类型，获取默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T orDefault(Class<T> clazz, T defaultVal) {
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        return defaultVal;
    }

    /**
     * 是否为数值类型
     */
    public boolean isNumber() {
        return NumberUtils.STANDARD_NUMBER_TYPES.contains(value.getClass());
    }

    /**
     * 获取数值类型
     */
    public Byte getByte() {
        return NumberUtils.convertNumberToTargetClass(get(), Byte.class);
    }

    public Short getShort() {
        return NumberUtils.convertNumberToTargetClass(get(), Short.class);
    }

    public Integer getInteger() {
        return NumberUtils.convertNumberToTargetClass(get(), Integer.class);
    }

    public Long getLong() {
        return NumberUtils.convertNumberToTargetClass(get(), Long.class);
    }

    public BigInteger getBigInteger() {
        return NumberUtils.convertNumberToTargetClass(get(), BigInteger.class);
    }

    public Float getFloat() {
        return NumberUtils.convertNumberToTargetClass(get(), Float.class);
    }

    public Double getDouble() {
        return NumberUtils.convertNumberToTargetClass(get(), Double.class);
    }

    public BigDecimal getBigDecimal() {
        return NumberUtils.convertNumberToTargetClass(get(), BigDecimal.class);
    }


}
