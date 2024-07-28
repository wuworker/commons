package com.wxl.commons.util.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Create by wuxingle on 2024/07/27
 * 二元组
 */
@EqualsAndHashCode
@ToString
public class Pair<T1, T2> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7707084543772711445L;

    private final T1 first;

    private final T2 second;

    private Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return new Pair<>(first, second);
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }
}
