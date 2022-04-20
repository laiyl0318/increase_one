package com.jiayi.common.checks;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author cjw
 * @date 2020-12-08
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckAesValidator.class)
@Documented
@Repeatable(CheckAes.List.class)
public @interface CheckAes {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 空字符串,空集合（不进行验证）
     *
     * @return
     */
    boolean ignoreEmpty() default false;

    /**
     * 验证解析出的值是否必须为数字
     */
    boolean mustNum() default false;

    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        CheckAes[] value();
    }
}
