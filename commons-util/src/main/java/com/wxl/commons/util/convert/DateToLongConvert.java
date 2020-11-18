package com.wxl.commons.util.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * Create by wuxingle on 2020/11/12
 * date转long
 */
public class DateToLongConvert implements Converter<Date, Long> {

    @Nullable
    @Override
    public Long convert(Date source) {
        return source.toInstant().toEpochMilli();
    }
}
