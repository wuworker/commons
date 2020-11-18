package com.wxl.commons.jdbc;

import com.wxl.commons.util.convert.Getters;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Create by wuxingle on 2020/11/11
 * 按一定规则自动转换为类
 */
public class AutoClassJdbcMapping<T> implements JdbcMapping<T> {

    private final Class<T> clazz;

    private final NameMapping nameMapping;

    private final boolean isSimpleType;

    public AutoClassJdbcMapping(Class<T> clazz) {
        this(clazz, null);
    }

    public AutoClassJdbcMapping(Class<T> clazz, @Nullable NameMapping nameMapping) {
        this.clazz = clazz;
        this.nameMapping = Optional.ofNullable(nameMapping).orElse(NameMapping.toCamel());
        this.isSimpleType = isSimpleType(clazz);
    }

    /**
     * 行数据映射为对象
     *
     * @param data 行数据
     * @return 对象
     */
    @Override
    public T mapping(Map<String, Object> data) {
        // 简单类型转换
        if (isSimpleType) {
            if (data.isEmpty()) {
                return Getters.of(null).get(clazz);
            }
            if (data.size() > 1) {
                throw new IllegalStateException("sql query column more than one! " + data.keySet());
            }
            return Getters.of(data.values().iterator().next()).get(clazz);
        }

        // java bean
        T bean = BeanUtils.instantiateClass(clazz);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = nameMapping.mapping(entry.getKey());
            Field field = ReflectionUtils.findField(clazz, fieldName);

            if (field != null) {
                field.setAccessible(true);
                Object value = Getters.of(entry.getValue()).get(field.getType());
                ReflectionUtils.setField(field, bean, value);
            }
        }
        return bean;
    }

    /**
     * 简单类型
     *
     * @param clazz
     * @return
     */
    protected boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Character.class == clazz
                || Boolean.class == clazz
                || Date.class.isAssignableFrom(clazz)
                || Object.class == clazz;
    }

}
