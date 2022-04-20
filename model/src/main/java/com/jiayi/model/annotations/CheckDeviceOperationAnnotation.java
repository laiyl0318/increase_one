package com.jiayi.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设备操作前的验证：
 * 需要验证设备在线、及设备操作状态注解
 * @author cjw
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
public @interface CheckDeviceOperationAnnotation {
}
