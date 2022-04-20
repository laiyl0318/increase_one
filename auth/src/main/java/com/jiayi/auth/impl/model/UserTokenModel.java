package com.jiayi.auth.impl.model;

import com.jiayi.common.util.RandomUtil;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.Data;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author cjw on 2017/11/2.
 */
@Data
public class UserTokenModel implements Serializable {
    /**
     * 生成token 时增加的salt
     */
    public static final String GENERATE_SALT = "jiayi";
    private static final long serialVersionUID = -4993956187020136510L;
    /**
     * 平台标识
     */
    private String platform;
    /**
     * 登录账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户ip
     */
    private String ip;
    /**
     * 用户agent
     */
    private String agent;
    /**
     * 用户登录后的token
     */
    private String token;

    public UserTokenModel() {

    }

    public UserTokenModel(String username, String password, String ip, String agent) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        if (agent.contains("WeChat") || agent.contains("Weixin")||agent.contains("wechatdevtools")) {
            this.agent = agent.substring(0, 140);
        }else {
            this.agent = agent;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void setEncodeToken(String sourceTokenValue) {
        this.token = BaseEncoding.base64Url().encode(Hashing.sha256().hashUnencodedChars(sourceTokenValue).asBytes());

    }

    /**
     * 生成用户token
     */
    public String generateUserToken() {
        if (username == null || password == null) {
            return null;
        }
        String tokenStr = password + GENERATE_SALT + username + RandomUtil.randomAlphaAndNum(10);
        token =
                BaseEncoding.base64Url().encode(Hashing.sha256().hashString(tokenStr, StandardCharsets.UTF_8).asBytes());
        return token;
    }

    /**
     * 生成用户在redis中的key
     */
    public String generateRedisToken() {
        if (token == null) {
            token = generateUserToken();
        }
        if (token != null) {
            String tokenStr = token + GENERATE_SALT + agent;
            return BaseEncoding.base64Url().encode(
                    Hashing.sha256().hashString(tokenStr, StandardCharsets.UTF_8).asBytes());
        } else {
            return null;
        }
    }

    /**
     * 生成不同平台用户在redis中的key
     *
     * @return
     */
    public String generatePlatformRedisToken() {
        if (token == null) {
            token = generateUserToken();
        }
        if (token == null) {
            return null;
        }
        String tokenStr = token + platform + GENERATE_SALT;
        return BaseEncoding.base64Url().encode(
                Hashing.sha256().hashString(tokenStr, StandardCharsets.UTF_8).asBytes());
    }

}
