package com.jiayi.common.checks;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 验证手机号
 *
 * @author cjw
 * @date 2020-12-08
 */
public class CheckIdCardValidator implements ConstraintValidator<CheckIdCard, String> {
    private static final String PATTERN_ID_CARD_REGEX = "^(\\d|[a-z]|[A-Z]){18}$";

    @Override
    public void initialize(CheckIdCard constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        return Pattern.matches(PATTERN_ID_CARD_REGEX, value);
    }

}
