package com.wxl.commons.util;

import org.junit.Test;

import java.util.stream.IntStream;

/**
 * Create by wuxingle on 2020/10/12
 * 随机数测试
 */
public class RandomUtilsTest {

    @Test
    public void randomNum() {
        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextBoolean()));

        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextInt()));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextInt(100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextInt(0, 100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextLong()));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextLong(100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextLong(0, 100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextFloat()));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextFloat(100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextFloat(0, 100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextDouble()));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextDouble(100)));
        System.out.println("-----------------------------");

        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.nextDouble(0, 100)));
        System.out.println("-----------------------------");
    }

    @Test
    public void randomDate() {
        IntStream.range(0, 10)
                .forEach(i -> {
                    String d = RandomUtils.randomDate(
                            "2000-01-01", "2020-10-01", "yyyy-MM-dd");
                    System.out.println(d);
                });
    }

    @Test
    public void randomMobile() {
        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.randomMobile()));
    }

    @Test
    public void randomEmail() {
        IntStream.range(0, 10)
                .forEach(i -> System.out.println(RandomUtils.randomEmail(5, 10)));
    }
}