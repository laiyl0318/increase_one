package com.jiayi.config;

import com.jiayi.common.exception.BusinessException;
import com.jiayi.model.enums.CommonResultEnum;
import com.jiayi.model.support.ResponseUtil;
import com.jiayi.model.support.response.SysResponse;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sun.security.validator.ValidatorException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cjw
 * @date 2020-10-29
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfig {
    public static final String FIELD_STR = "field :";

    private SysResponse parseFieldErrors(List<FieldError> fieldErrors) {
        String firstError = null;
        List<Map<String, String>> resultObject = Lists.newArrayList();
        for (FieldError fieldError : fieldErrors) {
            String errorMsg;
            if (fieldError.isBindingFailure()) {
                errorMsg = "参数数据格式错误";
            } else {
                errorMsg = fieldError.getDefaultMessage();
            }
            if (Strings.isNullOrEmpty(firstError)) {
                firstError = errorMsg;
            }
            resultObject.add(ImmutableMap.of(fieldError.getField(),
                    Strings.isNullOrEmpty(errorMsg) ? "未知错误" : errorMsg));
        }
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, firstError, resultObject);
    }

    @ExceptionHandler({BindException.class})
    public SysResponse handleBindException(BindException e) {
        return parseFieldErrors(e.getFieldErrors());
    }

    @ExceptionHandler({ValidatorException.class})
    public SysResponse handleValidatorException(ValidatorException e) {
        return ResponseUtil.failure(e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public SysResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        String message = e.getMessage();
        String field = "";
        if (!Strings.isNullOrEmpty(message) && message.contains(FIELD_STR)) {
            field = message.substring(message.lastIndexOf(FIELD_STR) + 8).trim();
        }
        if (Strings.isNullOrEmpty(field) && !Strings.isNullOrEmpty(message)
                && message.contains("[\"") && message.contains("\"]")
        ) {
            field = message.substring(message.lastIndexOf("[\"") + 2, message.lastIndexOf("\"]"));
        }
        if (!Strings.isNullOrEmpty(field)) {
            return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "参数[ " + field + " ]数据格式错误");
        }
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "参数数据格式错误");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public SysResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return parseFieldErrors(e.getBindingResult().getFieldErrors());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public SysResponse handleConstraintViolationException(ConstraintViolationException e) {
        Set<String> messages = Sets.newHashSet();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String firstErrorStr = null;
        for (ConstraintViolation violation : violations) {
            String errorStr = violation.getMessage();
            messages.add(errorStr);
            if (Strings.isNullOrEmpty(firstErrorStr)) {
                firstErrorStr = errorStr;
            }
        }
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, firstErrorStr, messages);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public SysResponse handleException(HttpMediaTypeNotSupportedException e) {
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "请求消息数据类型不支持");
    }

    @ExceptionHandler(BusinessException.class)
    public SysResponse handleBusinessException(BusinessException e) {
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public SysResponse handleException(Exception e) {
        log.error("系统处理异常", e);
        return ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "系统异常");
    }


}
