package com.jiayi.common.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成随机字符的工具类
 * Created by cjw on 2017/9/4.
 *
 * @author cjw
 */
public class RandomUtil {
    public static Integer RANDOM_STR_LENGTH = 9;

    private static String randomSourceString(int length, String sourceStr) {
        int sourceStrLen = sourceStr.length();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int subNum = random.nextInt(sourceStrLen);
            builder.append(sourceStr.charAt(subNum));
        }
        return builder.toString();
    }

    /**
     * 生成定长的字母随机字符串
     *
     * @param length
     * @return
     */
    public static String randomAlpha(int length) {
        return randomSourceString(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXCustomer");
    }

    /**
     * 生成定长的字母、数字随机字符串
     *
     * @param length
     * @return
     */
    public static String randomAlphaAndNum(int length) {
        return randomSourceString(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXCustomer0123456789");
    }

    /**
     * 生成定长的数字随机字符串,最大长度9位
     *
     * @param length
     * @return
     */
    public static String randomNumber(int length) {
        if (length > RANDOM_STR_LENGTH) {
            length = 9;
        }
        String origin = "1";
        String bound = "9";
        if (length > 1) {
            for (int i = 1; i < length; i++) {
                origin += "0";
                bound += "9";
            }
        }
        int originInt = Integer.valueOf(origin);
        int boundInt = Integer.valueOf(bound);
        return String.valueOf(ThreadLocalRandom.current().nextInt(originInt, boundInt));
    }

    /**
     * 生成定长的随机字符串（字母、数字、特殊字符串，ASCII）
     *
     * @param length
     * @return
     */
    public static String randomStr(int length) {
        StringBuilder builder = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            builder.append((char) random.nextInt(33, 127));
        }
        return builder.toString();
    }

    public static int getRandomInt(int max) {
        int min = 0;
        Random random = new Random();

        int rv = random.nextInt(max) % (max - min + 1) + min;
        return rv;
    }

    public static void main(String[] args) {
        System.out.println(randomNumber(9));
    }
}
