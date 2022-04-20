package com.jiayi.common.enums;

/**
 * 生成编码类型枚举
 *
 * @author Katrina
 * @date 2020-10-28
 */
public enum GenerateCodeEnum {
    /**
     * 编码类型
     */
    FILE("ATT", "上传文件", (byte) 101),
    IMAGE("IMG", "上传图片", (byte) 102),
    ;
    /**
     * 前缀
     */
    private String prefix;
    /**
     * 描述
     */
    private String description;
    /**
     * 值
     */
    private byte value;

    GenerateCodeEnum(String prefix, String description, byte value) {
        this.prefix = prefix;
        this.description = description;
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean equals(Byte value) {
        return Byte.valueOf(this.value).equals(value);
    }
}
