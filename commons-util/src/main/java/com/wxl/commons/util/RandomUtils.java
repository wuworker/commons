package com.wxl.commons.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

/**
 * Create by wuxingle on 2020/10/13
 * 随机数生成
 */
public class RandomUtils {

    /**
     * 手机号码前缀
     * 移动：134/135/136/137/138/139/150/151/152/157/158/159/182/183/184/187/188/147/178
     * 联通：130/131/132/155/156/185/186/145/176
     * 电信：133/153/180/181/189/177
     * 虚拟运营商:170
     */
    private static final String[] MOBILE_PHONE_PREFIX = (
            "134/135/136/137/138/139/150/151/152/157/158/159/182/183/184/187/188/147/178/"
                    + "130/131/132/155/156/185/186/145/176/"
                    + "133/153/180/181/189/177"
    ).split("/");

    /**
     * 邮箱后缀
     */
    private static final String[] EMAIL_SUFFIX = (
            "@gmail.com,@yahoo.com,@msn.com,@hotmail.com,"
                    + "@aol.com,@ask.com,@live.com,@qq.com,@0355.net,@163.com,@163.net,@263.net,@3721.net,"
                    + "@yeah.net,@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn"
    ).split(",");

    private static final Random RANDOM = new Random();

    //-----------------随机数值------------------------

    /**
     * 随机boolean
     */
    public static boolean nextBoolean() {
        return nextBoolean(RANDOM);
    }

    public static boolean nextBoolean(Random random) {
        return random.nextBoolean();
    }

    /**
     * 随机int
     */
    public static int nextInt() {
        return nextInt(RANDOM);
    }

    public static int nextInt(Random random) {
        return random.nextInt();
    }

    public static int nextInt(int endExclusive) {
        return nextInt(RANDOM, endExclusive);
    }

    public static int nextInt(Random random, int endExclusive) {
        return random.nextInt(endExclusive);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return nextInt(RANDOM, startInclusive, endExclusive);
    }

    public static int nextInt(Random random, int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + random.nextInt(endExclusive - startInclusive);
    }

    /**
     * 随机double
     */
    public static double nextDouble() {
        return nextDouble(RANDOM);
    }

    public static double nextDouble(Random random) {
        return random.nextDouble();
    }

    public static double nextDouble(int endExclusive) {
        return nextDouble(RANDOM, endExclusive);
    }

    public static double nextDouble(Random random, int endExclusive) {
        return nextDouble(random, 0, endExclusive);
    }

    public static double nextDouble(int startInclusive, int endExclusive) {
        return nextDouble(RANDOM, startInclusive, endExclusive);
    }

    public static double nextDouble(Random random, double startInclusive, double endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * random.nextDouble());
    }

    //-----------------随机字符串------------------------

    /**
     * uuid
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 随机数字字符串
     */
    public static String randomNumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomNumeric(RANDOM, nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomNumeric(Random random, int minLengthInclusive, int maxLengthExclusive) {
        return randomNumeric(random, nextInt(random, minLengthInclusive, maxLengthExclusive));
    }

    public static String randomNumeric(int count) {
        return randomNumeric(RANDOM, count);
    }

    public static String randomNumeric(Random random, int count) {
        return RandomStringUtils.random(count, 0, 0, false, true, null, random);
    }

    /**
     * 随机大小写字母
     */
    public static String randomAlphabetic(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphabetic(RANDOM, nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphabetic(Random random, int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphabetic(random, nextInt(random, minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphabetic(int count) {
        return randomAlphabetic(RANDOM, count);
    }

    public static String randomAlphabetic(Random random, int count) {
        return RandomStringUtils.random(count, 0, 0, true, false, null, random);
    }

    /**
     * 随机大小写字母和数字组合
     */
    public static String randomAlphanumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphanumeric(RANDOM, nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphanumeric(Random random, int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphanumeric(random, nextInt(random, minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphanumeric(int count) {
        return randomAlphanumeric(RANDOM, count);
    }

    public static String randomAlphanumeric(Random random, int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, random);
    }

    /**
     * 随机33-126的ascii字符
     */
    public static String randomAscii(int minLengthInclusive, int maxLengthExclusive) {
        return randomAscii(RANDOM, nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAscii(Random random, int minLengthInclusive, int maxLengthExclusive) {
        return randomAscii(random, nextInt(random, minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAscii(int count) {
        return randomAscii(RANDOM, count);
    }

    public static String randomAscii(Random random, int count) {
        return RandomStringUtils.random(count, 33, 126, false, false, null, random);
    }

    /**
     * 从候选字符中随机生成
     */

    public static String randomCandidate(int minLengthInclusive, int maxLengthExclusive, final String chars) {
        return randomCandidate(RANDOM, nextInt(minLengthInclusive, maxLengthExclusive), chars);
    }

    public static String randomCandidate(Random random, int minLengthInclusive, int maxLengthExclusive, final String chars) {
        return randomCandidate(random, nextInt(random, minLengthInclusive, maxLengthExclusive), chars);
    }

    public static String randomCandidate(int count, final String chars) {
        return randomCandidate(RANDOM, count, chars);
    }

    public static String randomCandidate(Random random, int count, final String chars) {
        return RandomStringUtils.random(count, 0, chars.length(), false, false, chars.toCharArray(), random);
    }


    /**
     * 生成随机日期
     *
     * @param startDate 开始日期字符串
     * @param endDate   结束日期字符串
     * @param pattern   格式化字符
     */
    public static String randomDate(String startDate, String endDate, String pattern) {
        return randomDate(RANDOM, startDate, endDate, pattern);
    }

    public static String randomDate(Random random, String startDate, String endDate, String pattern) {
        try {
            Date start = DateUtils.parseDate(startDate, pattern);
            Date end = DateUtils.parseDate(endDate, pattern);

            return DateFormatUtils.format(randomDate(random, start, end), pattern);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Date randomDate(Date start) {
        return randomDate(RANDOM, start, new Date());
    }

    public static Date randomDate(Random random, Date start) {
        return randomDate(random, start, new Date());
    }

    public static Date randomDate(Date start, Date end) {
        return randomDate(RANDOM, start, end);
    }

    public static Date randomDate(Random random, Date start, Date end) {
        long t = (long) ((end.getTime() - start.getTime()) * random.nextDouble()) + start.getTime();
        return new Date(t);
    }

    /**
     * 随机生成手机号
     */
    public static String randomMobile() {
        return randomMobile(RANDOM);
    }

    public static String randomMobile(Random random) {
        String prefix = MOBILE_PHONE_PREFIX[random.nextInt(MOBILE_PHONE_PREFIX.length)];
        int end = 11 - prefix.length();
        return prefix + randomNumeric(random, end);
    }

    /**
     * 随机生成邮箱
     */
    public static String randomEmail(int min, int max) {
        return randomEmail(RANDOM, nextInt(min, max));
    }

    public static String randomEmail(Random random, int min, int max) {
        return randomEmail(random, nextInt(random, min, max));
    }

    public static String randomEmail(int length) {
        return randomEmail(RANDOM, length);
    }

    public static String randomEmail(Random random, int length) {
        String suffix = EMAIL_SUFFIX[random.nextInt(EMAIL_SUFFIX.length)];
        return randomAlphabetic(random, length) + suffix;
    }

    /**
     * 随机对象
     */
    public static <T> List<T> randomObject(int count, Function<Random, T> generator) {
        return randomObject(RANDOM, count, generator);
    }

    public static <T> List<T> randomObject(Random random, int count, Function<Random, T> generator) {
        List<T> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(generator.apply(random));
        }
        return list;
    }
}

