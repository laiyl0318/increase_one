package com.jiayi.model.enums;

/**
 * 常用枚举判断是否
 *
 * @author cjw
 * @date 2020-10-19
 */
public enum CommonIsEnum {
    /**
     * 是 所有新的业务数据只能使用newValue的值来表示是否：1是，2否
     */
    TRUE( (byte) 1, "是", "可用", "启用"),
    /**
     * 否
     */
    FALSE( (byte) 2, "否", "不可用", "未启用");


    /**
     * 值
     */
    private Byte value;

    /**
     * 通用名称
     */
    private String name;

    /**
     * 是否可用名称
     */
    private String canUseName;

    /**
     * 启用|未启用名称
     */
    private String openName;

    CommonIsEnum(Byte value, String name, String canUseName, String openName) {
        this.value = value;
        this.name = name;
        this.canUseName = canUseName;
        this.openName = openName;
    }


    public static CommonIsEnum toEnum(Byte value) {
        CommonIsEnum commonIsEnum = CommonIsEnum.FALSE;
        for (CommonIsEnum isEnum : CommonIsEnum.values()) {
            if (isEnum.equals(value)) {
                commonIsEnum = isEnum;
            }
        }
        return commonIsEnum;
    }

    public Byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getCanUseName() {
        return canUseName;
    }

    public String getOpenName() {
        return openName;
    }

    public boolean equals(Byte newValue) {
        return this.value.equals(newValue);
    }
}
