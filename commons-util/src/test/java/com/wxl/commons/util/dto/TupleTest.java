package com.wxl.commons.util.dto;

import org.junit.jupiter.api.Test;

/**
 * Create by wuxingle on 2024/07/27
 */
public class TupleTest {

    @Test
    public void test() {
        var pair = Pair.of("aa", "bb");
        System.out.println(pair.first() + ", " + pair.second());
        System.out.println(pair);

        var tripe = Tripe.of("aa", "bb", "cc");
        System.out.println(tripe.first() + ", " + tripe.second() + ", " + tripe.third());
        System.out.println(tripe);
    }
}
