package com.jiayi.common.util;

import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具
 * @author wyq
 * @date 2020-09-23 14:02
 */

public class DateUtil {
    /**
     * 日期格式yyyy-MM-dd字符串常量
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 某天开始时分秒字符串常量  00:00:00
     */
    public static final String DAY_BEGIN_STRING_HHMMSS = " 00:00:00";
    /**
     * 某天结束时分秒字符串常量  00:00:00
     */
    public static final String DAY_END_STRING_HHMMSS = " 23:59:59";

    /**
     * 日期格式yyyy-MM-dd HH:mm:ss字符串常量
     */
    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期格式yyyy-MM-dd HH:mm:ss字符串常量无空格 精确到毫秒(加三位数)
     */
    public static final String FULL_DATE_FORMAT_TRIM = "yyyyMMddHHmmssSSS";

    /**
     * 数据库默认时间
     */
    public static final String MIN_DEFAULT_TIME = "1000-01-01 00:00:00";

    /**
     * 时间对象转换为日期对象
     * @param localDateTime
     * @return
     */
    public static String timeToDateStr(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * 解析Start日期字符串
     *
     * @param timeString
     * @return
     */
    public static LocalDateTime transStartTimeString(String timeString) {
        if (ObjectUtils.isEmpty(timeString)) {
            return null;
        }
        return LocalDateTime.parse(timeString.concat(DAY_BEGIN_STRING_HHMMSS), DateTimeFormatter.ofPattern(FULL_DATE_FORMAT));
    }

    /**
     * 解析End日期字符串
     *
     * @param timeString
     * @return
     */
    public static LocalDateTime transEndTimeString(String timeString) {
        if (ObjectUtils.isEmpty(timeString)) {
            return null;
        }
        return LocalDateTime.parse(timeString.concat(DAY_END_STRING_HHMMSS), DateTimeFormatter.ofPattern(FULL_DATE_FORMAT));
    }

    /**
     * 获取当前周的第一天 yyyy-MM-dd  HH:mm:ss
     *
     * @return
     */
    public static LocalDateTime getCurrentWeekFirstDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentWeekStart = now.minusDays(LocalDate.now().getDayOfWeek().ordinal()).withHour(0).withMinute(0).withSecond(0);
        return currentWeekStart;
    }

    /**
     * 获得上周第一天:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getLastWeekFirstDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeekStart = now.minusDays(LocalDate.now().getDayOfWeek().ordinal() + 7).withHour(0).withMinute(0).withSecond(0);
        return lastWeekStart;
    }

    /**
     * 获得上周最后一天:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getLastWeekLastDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeekEnd = now.minusDays(LocalDate.now().getDayOfWeek().ordinal() + 1).withHour(23).withMinute(59).withSecond(59);
        return lastWeekEnd;
    }

    /**
     * 获取当前月的第一天
     *
     * @return
     */
    public static LocalDateTime getCurrentMonthFirstDay() {
        LocalDate currentMonthStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        return LocalDateTime.of(currentMonthStart, LocalTime.MIN);
    }

    /**
     * 获得上月第一天:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getLastMonthFirstDay() {
        LocalDate lastMonthStart = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        return LocalDateTime.of(lastMonthStart, LocalTime.MIN);
    }

    /**
     * 获得上月最后一天:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getLastMonthLastDay() {
        LocalDate lastMonthEnd = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return LocalDateTime.of(lastMonthEnd, LocalTime.MAX);
    }

    /**
     * 获取昨天的开始时间:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getYesterdayStartTime() {
        LocalDate yesterdary = LocalDate.now().minusDays(1);
        LocalDateTime yesStart = LocalDateTime.of(yesterdary, LocalTime.MIN);
        return yesStart;
    }

    /**
     * 获取昨天的结束时间:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getYesterdayEndTime() {
        LocalDate yesterdary = LocalDate.now().minusDays(1);
        LocalDateTime yesEnd = LocalDateTime.of(yesterdary, LocalTime.MAX);
        return yesEnd;
    }

    /**
     * 获取当天的开始时间:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getTodayStartTime() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return todayStart;
    }

    /**
     * 获取当天的结束时间:yyyy-MM-dd  HH:mm:ss
     */
    public static LocalDateTime getTodayEndTime() {
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return todayEnd;
    }

    /**
     * 解析LocalDateTime日期为字符串
     *
     * @param localDateTime
     * @return
     */
    public static String transTimeToStrWithOutMinTime(LocalDateTime localDateTime) {
        if (ObjectUtils.isEmpty(localDateTime)) {
            return "-";
        }
        String timeStr = localDateTime.format(DateTimeFormatter.ofPattern(FULL_DATE_FORMAT));
        if (MIN_DEFAULT_TIME.equals(timeStr)) {
            return "-";
        }
        return timeStr;
    }

    /**
     * 解析LocalDateTime日期为字符串
     *
     * @param localDateTime
     * @return
     */
    public static String transTimeToStrWithOutMinTimeTrim(LocalDateTime localDateTime) {
        if (ObjectUtils.isEmpty(localDateTime)) {
            return "-";
        }
        String timeStr = localDateTime.format(DateTimeFormatter.ofPattern(FULL_DATE_FORMAT_TRIM));
        if (MIN_DEFAULT_TIME.equals(timeStr)) {
            return "-";
        }
        return timeStr;
    }
}
