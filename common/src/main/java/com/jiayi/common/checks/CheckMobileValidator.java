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
public class CheckMobileValidator implements ConstraintValidator<CheckMobile, String> {
    private static final String PATTERN_MOBILE_REGEX = "^-?[0-9]+";
    private static final Integer MOBILE_PHONE_LENGTH = 11;

    @Override
    public void initialize(CheckMobile constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        if (value.length() < MOBILE_PHONE_LENGTH) {
            return false;
        }
        return Pattern.matches(PATTERN_MOBILE_REGEX, value);
    }

}
