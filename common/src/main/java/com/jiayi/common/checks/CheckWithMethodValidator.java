package com.jiayi.common.checks;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author cjw
 * @date 2020-12-08
 */
public class CheckWithMethodValidator implements ConstraintValidator<CheckWithMethod, Object> {
    private Class<?> checkClazz;

    private String checkMethod;

    @Override
    public void initialize(CheckWithMethod constraintAnnotation) {
        this.checkClazz = constraintAnnotation.checkClazz();
        this.checkMethod = constraintAnnotation.checkMethod();
    }

    @Override
    public boolean isValid(Object checkObj, ConstraintValidatorContext context) {
        if (checkObj == null) {
            return true;
        }
        try {
            Method invokeMethod = null;
            Method[] methods = checkClazz.getDeclaredMethods();
            for (Method oneMethod : methods) {
                if (oneMethod.getName().equals(checkMethod)) {
                    invokeMethod = oneMethod;
                    break;
                }
            }
            if (invokeMethod == null) {
                return false;
            }
            return (Boolean) invokeMethod.invoke(checkClazz.newInstance(), checkObj);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            return false;
        }
    }

}
