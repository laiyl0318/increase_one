package com.jiayi.common.checks;

import com.jiayi.common.util.AesUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author cjw
 * @date 2020-12-08
 */
public class CheckAesValidator implements ConstraintValidator<CheckAes, Object> {
    private static final String PATTERN_NUM_REGEX = "^[0-9]*$";

    private boolean mustNum;

    private boolean ignoreEmpty;

    @Override
    public void initialize(CheckAes constraintAnnotation) {
        this.mustNum = constraintAnnotation.mustNum();
        this.ignoreEmpty = constraintAnnotation.ignoreEmpty();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        if (value instanceof List) {
            Collection valueList = (Collection) value;
            if (CollectionUtils.isEmpty(valueList)) {
                return ignoreEmpty;
            }
            return valueList.stream().allMatch(this::checkCanDecrypt);
        } else if (checkIsJsonAr(value)) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> jsonAr = null;
            try {
                jsonAr = objectMapper.readValue(value.toString(), new TypeReference<List<String>>() {
                });
            } catch (Exception ignore) {
            }
            if (CollectionUtils.isEmpty(jsonAr)) {
                return ignoreEmpty;
            }
            return jsonAr.stream().allMatch(this::checkCanDecrypt);
        } else {
            if (Strings.isNullOrEmpty(value.toString())) {
                return ignoreEmpty;
            }
            return checkCanDecrypt(value);
        }
    }

    /**
     * ??????????????????json????????????
     *
     * @param value
     * @return
     */
    private boolean checkIsJsonAr(Object value) {
        return value instanceof String && Pattern.matches("^\\[[\\s\\S]*\\]$", value.toString());
    }

    /**
     * ????????????????????????
     *
     * @param value
     * @return
     */
    private boolean checkCanDecrypt(Object value) {
        String args = AesUtil.decryptUrlSafe(Objects.toString(value));
        // ????????????????????????????????????false
        if (Strings.isNullOrEmpty(args)) {
            return false;
        }
        // ??????????????????????????????????????????
        if (!Pattern.matches(PATTERN_NUM_REGEX, args)) {
            return !mustNum;
        }
        return true;
    }

}

