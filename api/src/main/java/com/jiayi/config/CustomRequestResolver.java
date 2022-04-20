package com.jiayi.config;//package com.jiayi.config;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.base.Strings;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
//import org.springframework.web.method.support.ModelAndViewContainer;
//import sun.security.validator.ValidatorException;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.util.Objects;
//import java.util.Set;
//
///**
// * @author cjw
// * @date 2020-10-14
// */
//@Component
//@Slf4j
//public class CustomRequestResolver extends HandlerMethodArgumentResolverComposite {
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return false;
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        OpenApplyRequest formObj = parameter.getParameterAnnotation(OpenApplyRequest.class);
//        Class<?>[] checkGroups = formObj.checkGroups();
//        String key = Strings.isNullOrEmpty(formObj.value()) ? parameter.getParameterName() : formObj.value();
//
//        Class<?> paramType = parameter.getParameterType();
//        Object checkObject = null;
//        try {
//            checkObject = JSON.parseObject(webRequest.getParameter(key), paramType);
//        } catch (Exception ex) {
//            log.error("解析发货申请表单数据失败", ex);
//        }
//        if (Objects.isNull(checkObject)) {
//            checkObject = paramType.newInstance();
//        }
//
//        // 验证
//        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
//        Validator validator = vf.getValidator();
//        Set<ConstraintViolation<Object>> result = validator.validate(checkObject, checkGroups);
//
//        if (!CollectionUtils.isEmpty(result)) {
//            for (ConstraintViolation<Object> violation : result) {
//                throw new ValidatorException(violation.getMessage());
//            }
//        }
//
//        return checkObject;
//    }
//}
