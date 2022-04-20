package com.jiayi.model.annotations;

import java.lang.annotation.*;

/**
 * 统计方法执行时间注解
 *
 * @author cjw
 * @date 2020-10-15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimeCountAnnotation {
}

