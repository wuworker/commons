package com.wxl.commons.util;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;

/**
 * Create by wuxingle on 2020/10/15
 * 用与把value转为目标值
 * ConversionService简单包装
 */
public class Getters {

    @Nullable
    private final Object value;

    @Nullable
    private Class<?> valueClass;

    private ConversionService conversionService;

    private Getters(@Nullable Object value) {
        this.value = value;
        this.conversionService = DefaultConversionService.getSharedInstance();
        if (this.value != null) {
            this.valueClass = this.value.getClass();
        }
    }

    public static Getters of(@Nullable Object value) {
        return new Getters(value);
    }

    public Getters hint(Class<?> clazz) {
        this.valueClass = clazz;
        return this;
    }

    public Getters setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
        return this;
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    /**
     * 已知目标类型下获取
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) value;
    }

    /**
     * 转换为目标类型
     */
    @Nullable
    public <T> T get(Class<T> clazz) {
        return conversionService.convert(value, clazz);
    }

    /**
     * 不是目标类型，获取默认值
     */
    @Nullable
    public <T> T orElse(Class<T> clazz, T defaultVal) {
        if (conversionService.canConvert(valueClass, clazz)) {
            return conversionService.convert(value, clazz);
        }

        return defaultVal;
    }

    /**
     * 是否支持转为目标类型
     */
    public boolean support(Class<?> clazz) {
        return conversionService.canConvert(valueClass, clazz);
    }

}
