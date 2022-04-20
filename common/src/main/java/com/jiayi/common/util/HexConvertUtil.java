package com.jiayi.common.util;

/**
 * 进制转换工具
 *
 * @author laiyilong
 */
public class HexConvertUtil {
    public static final int BYTE_START_INDEX = 7;

    /**
     * 字节数据转换为二进制字节数组
     *
     * @param b
     * @return
     */
    public static byte[] getBinaryArray(byte b) {
        byte[] ba = new byte[8];
        for (int i = BYTE_START_INDEX; i >= 0; i--) {
            ba[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return ba;
    }

    /**
     * 字节转二进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToBinaryStr(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (b & 0x1);
    }

    /**
     * hexString数组转换成byte数组
     * @param strings
     * @return
     */
    public static int[] parseHexStringArrayToIntArray(String[] strings) {
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = ((Character.digit(strings[i].charAt(0), 16) << 4) + Character.digit(strings[i].charAt(1), 16));
        }
        return ints;
    }
}
