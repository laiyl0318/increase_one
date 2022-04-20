package com.jiayi.model.support.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractValidation implements Serializable {
    public static final String PROFILES_COMMON = "common";
    public static final String MSG_SUFFIX_NOT_NULL_BLANK = "不可为空";
    public static final String MSG_SUFFIX_ONLY_LETTER_NUMBER = "仅支持输入字母和数字";
    public static final String MSG_SUFFIX_FORMAT_ERROR = "格式错误";
    public static final String MSG_PREFIX_VALUE_INVALID = "无效的";
    public static final String MSG_SUFFIX_ILLEGAL_CHARACTER = "不可使用非法字符";
    public static final String MSG_SUFFIX_REPEAT = "不可重复";
    public static final String MSG_SUFFIX_FILL = "请填写";
    protected static final String MSG_TITLE = "必要参数: ";
    protected static final String MSG_OTHER_TITLE = "选填参数: ";
    private static final long serialVersionUID = -5195015261731455140L;
    private String debug = "false";
}
