package com.jiayi.model.support;

import com.jiayi.model.enums.CommonResultEnum;
import com.jiayi.model.support.response.SysResponse;
import org.springframework.stereotype.Service;

/**
 * @author cjw
 * @date 2020-10-27
 */
@Service
public class ResponseUtil {

    public static SysResponse success() {
        return new SysResponse(CommonResultEnum.SUCCEED.getCode(), CommonResultEnum.SUCCEED.getMsg(), null);
    }

    public static SysResponse success(Object resultObject) {
        return new SysResponse(CommonResultEnum.SUCCEED.getCode(), CommonResultEnum.SUCCEED.getMsg(), resultObject);
    }

    public static SysResponse success(String msg, Object resultObject) {
        return new SysResponse(CommonResultEnum.SUCCEED.getCode(), msg, resultObject);
    }

    public static SysResponse failure(String msg) {
        return new SysResponse(CommonResultEnum.BUSINESS_FAILED.getCode(), msg, null);
    }

    public static SysResponse failure(String msg, Object resultObject) {
        return new SysResponse(CommonResultEnum.BUSINESS_FAILED.getCode(), msg, resultObject);
    }

    public static SysResponse failure(CommonResultEnum commonResultEnum) {
        return new SysResponse(commonResultEnum.getCode(), commonResultEnum.getMsg(), null);
    }

    public static SysResponse failure(CommonResultEnum commonResultEnum, String msg) {
        return new SysResponse(commonResultEnum.getCode(), msg, null);
    }

    public static SysResponse failure(CommonResultEnum commonResultEnum, String msg, Object resultObject) {
        return new SysResponse(commonResultEnum.getCode(), msg, resultObject);
    }
}
