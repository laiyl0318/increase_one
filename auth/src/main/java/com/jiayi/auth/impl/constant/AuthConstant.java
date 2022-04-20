package com.jiayi.auth.impl.constant;

/**
 * 用户权限验证-常量
 *
 * @author cjw
 * @date 2020-10-18
 */
public class AuthConstant {
    /**
     * 管理平台-标识
     */
    public static final String PLATFORM_WEB = "web";
    /**
     * 微信-标识
     */
    public static final String PLATFORM_WECHAT = "wechat";
    /**
     * 系统默认无权限用户角色名
     */
    public final static String SECURITY_NO_PERMISSION_ROLE = "NO_PERMISSION_ROLE";
    /**
     * 权限认证角色前缀
     */
    public final static String SECURITY_ROLE_PREFIX = "ROLE_";

    public final class LoginState {
        /**
         * 用户登录状态保存时间，单位：秒
         */
        public static final int TIME = 7200;

        /**
         * 微信用户登录状态保存时间，单位：天
         */
        public static final int WECHAT_DAY = 15;
    }

    public final class RequestHeader {
        public static final String USER_AGENT = "User-Agent";
        public static final String AUTHORIZATION = "Authorization";
    }
}
