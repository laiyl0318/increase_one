package com.jiayi.model.enums;

/**
 * 分批处理类型枚举
 *
 * @author cjw
 * @date 2020-10-15
 */
public enum BatchExecuteTypeEnum {
    /**
     * 按传参的集合 分批执行方法
     */
    EXECUTE((byte) 1, "按参数分批执行"),
    /**
     * 按传参的集合 分批查询方法
     */
    QUERY((byte) 2, "按参数分批查询"),
    /**
     *
     */
    RESULT_QUERY((byte) 3, "按查询结果分批查询");
    /**
     * 枚举值
     */
    private Byte value;

    /**
     * 值描述
     */
    private String desc;

    BatchExecuteTypeEnum(byte value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
