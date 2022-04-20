package com.jiayi.auth.security.interfaces;

/**
 * 配置接口
 * @author laiyilong
 */
public interface CustomerSecurityProperty {
    /**
     * 无需权限系统处理的uri
     *
     * @return
     */
    String[] ignorePath();

    /**
     * 登录处理接口地址
     *
     * @return
     */
    String loginUrl();

    /**
     * 登出处理接口地址
     *
     * @return
     */
    String logoutUrl();

    /**
     * 登录表单账号字段key
     */
    String userNameKey();

    /**
     * 登录表单密码字段key
     */
    String passWordKey();

    /**
     * 识别用户登录的token key
     *
     * @return
     */
    String loginToken();

    /**
     * 验证码字段key
     *
     * @return
     */
    String verificationKey();
}
