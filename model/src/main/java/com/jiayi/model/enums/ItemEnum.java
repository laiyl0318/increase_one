package com.jiayi.model.enums;

/**
 * 权限项枚举
 *
 * @author cjw
 * @date 2020-10-26
 */
public enum ItemEnum {
    /**
     * 访问权限类型
     */
    HAVE_PERMISSION_ALLOWED("登录后符合权限可访问", (byte) 0),
    LOGIN_ALLOWED("登录后即可访问", (byte) 1),
    NO_LOGIN_ALLOWED("无需登录即可访问", (byte) 2);
    /**
     * 描述
     */
    private String desc;
    /**
     * 值
     */
    private byte value;

    ItemEnum(String desc, byte value) {
        this.desc = desc;
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public boolean equals(Byte value) {
        return Byte.valueOf(this.value).equals(value);
    }
}
