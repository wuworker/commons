package com.wxl.commons.util.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Create by wuxingle on 2024/07/27
 * 三元组
 */
@EqualsAndHashCode
@ToString
public class Tripe<T1, T2, T3> implements Serializable {

    @Serial
    private static final long serialVersionUID = -986252454151730044L;

    private final T1 first;

    private final T2 second;

    private final T3 third;

    private Tripe(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T1, T2, T3> Tripe<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new Tripe<>(first, second, third);
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }

    public T3 third() {
        return third;
    }
}
