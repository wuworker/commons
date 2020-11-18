package com.wxl.commons.util.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.function.Consumer;

/**
 * Create by wuxingle on 2020/10/15
 * 用与把value转为目标值
 * ConversionService简单包装
 */
public class Getters {

    /**
     * 默认转换器
     */
    private static DefaultConversionService DEFAULT_CONVERT = null;

    @Nullable
    private final Object value;

    @Nullable
    private Class<?> valueClass;

    private ConversionService conversionService;

    private Getters(@Nullable Object value) {
        this.value = value;
        if (this.value != null) {
            this.valueClass = this.value.getClass();
        }
    }

    /**
     * 获取默认转换器
     */
    private static DefaultConversionService getDefaultConvert() {
        if (DEFAULT_CONVERT == null) {
            synchronized (Getters.class) {
                if (DEFAULT_CONVERT == null) {
                    DEFAULT_CONVERT = new DefaultConversionService();
                    addInnerConvert(DEFAULT_CONVERT);
                }
            }
        }
        return DEFAULT_CONVERT;
    }

    /**
     * 添加内置转换器
     *
     * @param registry
     */
    private static void addInnerConvert(ConverterRegistry registry) {
        registry.addConverter(Date.class, Long.class, new DateToLongConvert());
        registry.addConverter(Number.class, Boolean.class, new NumberToBooleanConvert());
    }

    /**
     * 添加默认转换器
     */
    public static void addDefaultConvert(Consumer<ConverterRegistry> consumer) {
        consumer.accept(getDefaultConvert());
    }


    /**
     * 创建Getters
     *
     * @param value 待转换的值
     * @return
     */
    public static Getters of(@Nullable Object value) {
        return new Getters(value);
    }

    /**
     * 设置value类型
     */
    public Getters hint(Class<?> clazz) {
        this.valueClass = clazz;
        return this;
    }

    /**
     * 设置自定义转换器
     */
    public Getters setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
        return this;
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
        return getConvert().convert(value, clazz);
    }

    /**
     * 不是目标类型，获取默认值
     */
    @Nullable
    public <T> T orElse(Class<T> clazz, T defaultVal) {
        ConversionService convert = getConvert();
        if (convert.canConvert(valueClass, clazz)) {
            return convert.convert(value, clazz);
        }

        return defaultVal;
    }

    /**
     * 是否支持转为目标类型
     */
    public boolean support(Class<?> clazz) {
        return getConvert().canConvert(valueClass, clazz);
    }

    /**
     * 优先获取实例的转换器
     */
    private ConversionService getConvert() {
        if (conversionService == null) {
            return getDefaultConvert();
        }
        return conversionService;
    }
}
