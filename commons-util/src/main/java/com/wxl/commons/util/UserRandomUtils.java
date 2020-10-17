package com.wxl.commons.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

/**
 * Create by wuxingle on 2020/10/13
 * 用户相关随机数
 */
public class UserRandomUtils {

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

    //邮箱后缀
    private static final String[] EMAIL_SUFFIX = (
            "@gmail.com,@yahoo.com,@msn.com,@hotmail.com,"
                    + "@aol.com,@ask.com,@live.com,@qq.com,@0355.net,@163.com,@163.net,@263.net,@3721.net,"
                    + "@yeah.net,@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn"
    ).split(",");

    private static final Random RANDOM = new Random();

    /**
     * 生成随机日期
     *
     * @param startDate 开始日期字符串
     * @param endDate   结束日期字符串
     * @param pattern   格式化字符
     */
    public static String randomDate(String startDate, String endDate, String pattern) {
        try {
            Date start = DateUtils.parseDate(startDate, pattern);
            Date end = DateUtils.parseDate(endDate, pattern);

            return DateFormatUtils.format(randomDate(start, end), pattern);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Date randomDate(Date start, Date end) {
        long t = (long) ((end.getTime() - start.getTime()) * RANDOM.nextDouble()) + start.getTime();
        return new Date(t);
    }

    /**
     * 随机生成手机号
     */
    public static String randomMobile() {
        String prefix = MOBILE_PHONE_PREFIX[RANDOM.nextInt(MOBILE_PHONE_PREFIX.length)];
        int end = 11 - prefix.length();
        return prefix + RandomStringUtils.randomNumeric(end);
    }

    /**
     * 随机生成邮箱
     */
    public static String randomEmail(int length) {
        Assert.isTrue(length > 0, "length must > 0");
        String suffix = EMAIL_SUFFIX[RANDOM.nextInt(EMAIL_SUFFIX.length)];
        return RandomStringUtils.randomAlphabetic(length) + suffix;
    }

    public static String randomEmail(int min, int max) {
        return randomEmail(RANDOM.nextInt(max - min) + min);
    }
}

