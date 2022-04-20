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
@Constraint(validatedBy = CheckWithMethodValidator.class)
@Documented
@Repeatable(CheckWithMethod.List.class)
public @interface CheckWithMethod {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 验证方法所属类.class
     *
     * @return
     */
    Class<?> checkClazz();

    /**
     * 验证方法名
     *
     * @return
     */
    String checkMethod();

    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        CheckWithMethod[] value();
    }
}
