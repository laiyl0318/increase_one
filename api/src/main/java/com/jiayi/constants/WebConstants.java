package com.jiayi.constants;

/**
 * 网络使用常量
 *
 * @author wyq
 * @date 2020-10-26
 */
public class WebConstants {

    /**
     * URL访问前缀
     */
    public static final String RESTFUL_API_PREFIX = "/api";

    /**
     * URL访问前缀
     */
    public static final String RESTFUL_DATA_API_PREFIX = "/dataApi";

    /**
     * URL用户对外接口访问前缀
     */
    public static final String RESTFUL_OPEN_API_PREFIX = "/openApi";

    /**
     * 默认版本号
     */
    public static final String RESTFUL_DEFAULT_VERSION_PREFIX = "v1";

    /**
     * 下一个版本号
     */
    public static final String RESTFUL_NEXT_VERSION_PREFIX = "v2";

    public final class RequestHeader {
        public static final String USER_AGENT = "User-Agent";
        public static final String AUTHORIZATION = "Authorization";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    }
}


