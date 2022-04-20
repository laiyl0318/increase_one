package com.jiayi.model.enums;

/**
 * 操作日志对象类型枚举
 *
 * @author wyq
 * @date 2020-10-14
 */
public enum LogObjectTypeEnum {
    /**
     * 用户
     */
    USER("用户", (byte) 1),
    OTHER("其他", (byte) 100);

    private String desc;

    private Byte value;

    LogObjectTypeEnum(String desc, Byte value) {
        this.desc = desc;
        this.value = value;
    }

    /**
     * @param value
     * @return
     */
    public static String getDesc(byte value) {
        for (LogObjectTypeEnum item : LogObjectTypeEnum.values()) {
            if (item.value == value) {
                return item.desc;
            }
        }
        return null;
    }

    /**
     * @return desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @return value
     */
    public Byte getValue() {
        return value;
    }


    /**
     * @param value
     * @return
     */
    public boolean equals(Byte value) {
        return this.value.equals(value);
    }

}
