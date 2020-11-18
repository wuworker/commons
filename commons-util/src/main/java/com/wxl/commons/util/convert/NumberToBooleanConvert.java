package com.wxl.commons.util.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * Create by wuxingle on 2020/11/12
 * 数字转boolean
 */
public class NumberToBooleanConvert implements Converter<Number, Boolean> {

    /**
     * 大于0为真
     */
    @Nullable
    @Override
    public Boolean convert(Number source) {
        return source.intValue() > 0;
    }
}
