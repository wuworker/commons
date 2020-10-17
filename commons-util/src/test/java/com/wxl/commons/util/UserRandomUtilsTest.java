package com.wxl.commons.util;

import org.junit.Test;

import java.util.stream.IntStream;

/**
 * Create by wuxingle on 2020/10/12
 */
public class UserRandomUtilsTest {

    @Test
    public void randomDate() {
        IntStream.range(0, 10)
                .forEach(i -> {
                    String d = UserRandomUtils.randomDate(
                            "2000-01-01", "2020-10-01", "yyyy-MM-dd");
                    System.out.println(d);
                });
    }

    @Test
    public void randomMobile() {
        IntStream.range(0, 10)
                .forEach(i -> System.out.println(UserRandomUtils.randomMobile()));
    }

    @Test
    public void randomEmail() {
        IntStream.range(0, 10)
                .forEach(i -> System.out.println(UserRandomUtils.randomEmail(5, 10)));
    }
}