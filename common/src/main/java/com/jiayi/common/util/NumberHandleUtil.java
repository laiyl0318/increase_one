package com.jiayi.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数字处理类
 *
 * @author laiyilong
 */
public class NumberHandleUtil {
    /**
     * 单位元 转换为分的值
     *
     * @param objToTrans
     * @return
     */
    public static BigDecimal yuanToCent(BigDecimal objToTrans) {
        return objToTrans.multiply(new BigDecimal("100"));
    }

    /**
     * 单位元转换为分，并处理返回值为Integer
     *
     * @param objToTrans
     * @return
     */
    public static Integer yuanToCentInt(BigDecimal objToTrans) {
        return yuanToCent(objToTrans).intValue();
    }

    /**
     * 单位分转换为元
     *
     * @param objToTrans
     * @return
     */
    public static BigDecimal centToYuan(BigDecimal objToTrans) {
        return objToTrans.divide(new BigDecimal("100"), RoundingMode.HALF_UP);
    }

    /**
     * 单位分转换为元 从Integer 转换为 BigDecimal
     *
     * @param objToTrans
     * @return
     */
    public static BigDecimal centIntToYuan(Integer objToTrans) {
        return centToYuan(new BigDecimal(objToTrans));
    }

    /**
     * 计算数据占比,精确小数点后2位  单位百分比
     *
     * @param targetNum 计算数量
     * @param allNum    全部数量
     * @param scale     精确小数点
     * @return
     */
    public static BigDecimal computeRate(Integer targetNum, Integer allNum, Integer scale) {
        return new BigDecimal(targetNum).multiply(new BigDecimal(100)).divide(new BigDecimal(allNum), scale, RoundingMode.HALF_UP);
    }

    /**
     * 浮点数精确到小数点后两位，转字符创
     */
    public static final DecimalFormat dnf = new DecimalFormat("#.00");
    public static String doubleNormalFormat(Double v) {
        return dnf.format(v);
    }

}

