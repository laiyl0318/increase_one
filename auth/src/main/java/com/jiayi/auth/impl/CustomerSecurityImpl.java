package com.jiayi.auth.impl;

import com.jiayi.auth.impl.constant.AuthConstant;
import com.jiayi.auth.impl.model.CustomUserDetails;
import com.jiayi.auth.impl.model.UserTokenModel;
import com.jiayi.auth.impl.service.UserAuthService;
import com.jiayi.auth.security.exceptions.ErrorVerificationException;
import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import com.jiayi.auth.security.model.HandlerResponse;
import com.jiayi.auth.security.model.CustomerSecurityConfigAttributes;
import com.jiayi.common.exception.BusinessException;
import com.jiayi.common.util.HttpClientUtil;
import com.jiayi.common.util.RandomUtil;
import com.jiayi.model.dto.sys.UserDTO;
import com.jiayi.model.enums.CommonResultEnum;
import com.jiayi.model.support.ResponseUtil;
import com.jiayi.model.support.response.SysResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cjw
 * @date 2020-10-17
 */
@Slf4j
@Component
public class CustomerSecurityImpl extends BaseCustomerSecurity {
    /**
     * 登录错误次数触发验证码
     */
    public static final Integer USE_VERIFICATION_ERROR_NUM = 2;
    /**
     * 验证码保存不变的次数
     */
    public static final int VERIFICATION_KEEP_NUM = 3;
    @Resource
    private PasswordEncoder customPasswordEncoder;
    @Resource
    private UserAuthService userAuthService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplateForSession;

    public CustomerSecurityImpl() {
        super("/**");
    }

    /**
     * 不在此组件中验证权限的url
     *
     * @return
     */
    @Override
    public String[] ignorePath() {
        List<String> ignoreUrls = Lists.newArrayList();
        ignoreUrls.add("/openApi/**");
        ignoreUrls.add("/track.jsp");
        ignoreUrls.addAll(userAuthService.loadNoPmCheckUrls());
        return ignoreUrls.toArray(new String[0]);
    }

    /**
     * 登录用户token的key
     *
     * @return
     */
    @Override
    public String loginToken() {
        return AuthConstant.RequestHeader.AUTHORIZATION;
    }

    /**
     * 登录处理接口地址
     *
     * @return
     */
    @Override
    public String loginUrl() {
        return "/api/v1/user/login";
    }

    /**
     * 登出处理接口地址
     *
     * @return
     */
    @Override
    public String logoutUrl() {
        return "/api/v1/user/logout";
    }

    /**
     * 登录失败处理器
     *
     * @param httpServletRequest
     * @param e
     * @return
     */
    @Override
    public HandlerResponse loginFailureHandler(HttpServletRequest httpServletRequest, AuthenticationException e) {
        SysResponse response;
        if (e instanceof UsernameNotFoundException || e instanceof ErrorVerificationException) {
            response = ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, e.getMessage());
        } else {
            response = ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "用户名或密码错误");
        }

        String uId = loginUserUid(httpServletRequest);
        // 判断是否清空验证码
        if (checkRefreshVerification(uId)) {
            removeVerificationToRedis(uId);
        }
        // 记录用户登录失败次数
        Integer errorNum = recordLoginErrorNum(uId);
        Map<String, Integer> content = Maps.newHashMap();
        content.put("useVerification", 0);
        if (errorNum.compareTo(USE_VERIFICATION_ERROR_NUM) >= 0) {
            content.put("useVerification", 1);
        }
        response.setResponse(content);
        String result = "";
        try {
            result = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            log.error("loginFailureHandler error", ex);
            throw new BusinessException("系统异常");
        }
        return new HandlerResponse(HttpStatus.OK, result);
    }

    /**
     * 登录成功处理器
     *
     * @param httpServletRequest
     * @param authentication
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public HandlerResponse loginSuccessHandler(HttpServletRequest httpServletRequest, Authentication authentication) {
        try {
            // 登录成功，保存用户登录session
            CustomUserDetails userDetails = ((CustomUserDetails) authentication.getPrincipal());
            // 验证用户角色，如果为空则报错
            if (!userAuthService.userHasRoles(userDetails.getUserId()).isSuccess()) {
                return new HandlerResponse(HttpStatus.OK, objectMapper.writeValueAsString(ResponseUtil.failure("用户角色查询失败")));
            }
            // 生成usertoken
            UserTokenModel userToken = new UserTokenModel(userDetails.getUsername(), userDetails.getPassword(),
                    HttpClientUtil.getIpAddress(httpServletRequest), httpServletRequest.getHeader(AuthConstant.RequestHeader.USER_AGENT));
            // 用户userToken生成，仅可调用一次
            String userTokenStr = userToken.generateUserToken();
            // 用户保存redis token str生成
            String userRedisTokenStr = userToken.generateRedisToken();

            removeLoginErrorNum(loginUserUid(httpServletRequest));

            redisTemplateForSession.opsForValue().set(userRedisTokenStr, userAuthService.generateSessionValue(userDetails.getUserId().toString()), AuthConstant.LoginState.TIME, TimeUnit.SECONDS);
            return new HandlerResponse(HttpStatus.OK, objectMapper.writeValueAsString(ResponseUtil.success(ImmutableMap.of("token", userTokenStr))));
        } catch (Exception ex) {
            log.error("loginSuccessHandler error", ex);
            throw new BusinessException("服务处理异常");
        }

    }


    /**
     * 清除登录错误次数
     *
     * @param uId
     */
    @SuppressWarnings("unchecked")
    private void removeLoginErrorNum(String uId) {
        redisTemplateForSession.delete(loginErrorNumRedisKey(uId));
        removeVerificationToRedis(uId);
    }

    /**
     * 未登录拒绝处理器
     *
     * @param e
     * @return
     */
    @Override
    public HandlerResponse noLoginDeniedHandler(AuthenticationException e) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED_UNAUTHORIZED, "请登录")));
        } catch (Exception ex) {
            log.error("noLoginDeniedHandler error", ex);
            throw new BusinessException("服务处理异常");

        }
    }

    /**
     * 登录后没有权限拒绝处理器
     *
     * @param e
     * @return
     */
    @Override
    public HandlerResponse haveLoginDeniedHandler(AccessDeniedException e) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED_FORBIDDEN, "无权限")));
        } catch (Exception ex) {
            log.error("haveLoginDeniedHandler error", ex);
            throw new BusinessException("服务处理异常");

        }
    }

    /**
     * 登出成功处理器
     *
     * @param authentication
     * @return
     */
    @Override
    public HandlerResponse logoutSuccessHandler(Authentication authentication) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.success("用户已登出", null)));
        } catch (Exception ex) {
            log.error("logoutSuccessHandler error", ex);
            throw new BusinessException("服务处理异常");

        }
    }

    /**
     * 登出处理器
     *
     * @param httpServletRequest
     * @param authentication
     */
    @SuppressWarnings("unchecked")
    @Override
    public void logoutHandler(HttpServletRequest httpServletRequest, Authentication authentication) {
        String userToken = parseLoginToken(httpServletRequest, null);
        String redisToken = userAuthService.getRedisTokenStr(httpServletRequest, userToken);
        // 清除redis 数据
        try {
            if (redisTemplateForSession.hasKey(redisToken)) {
                redisTemplateForSession.delete(redisToken);
            }
        } catch (Exception ex) {
            log.error("用户登出处理器异常", ex);
        }
    }

    /**
     * 是否使用验证码
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    public Boolean useVerification(HttpServletRequest httpServletRequest) {
        return getLoginErrorNum(loginUserUid(httpServletRequest)).compareTo(USE_VERIFICATION_ERROR_NUM) >= 0;
    }

    /**
     * 验证码验证器
     *
     * @param httpServletRequest
     * @param s
     * @return
     */
    @Override
    public Boolean checkVerification(HttpServletRequest httpServletRequest, String s) {
        String redisVerification = getVerificationFromRedis(loginUserUid(httpServletRequest));
        return !Strings.isNullOrEmpty(redisVerification) && !Strings.isNullOrEmpty(s) &&
                redisVerification.equals(s);
    }


    /**
     * 记录用户登录失败次数
     *
     * @param uId
     * @return 失败次数
     */
    @SuppressWarnings("unchecked")
    private Integer recordLoginErrorNum(String uId) {
        String redisKey = loginErrorNumRedisKey(uId);
        Long errorNum = redisTemplateForSession.opsForValue().increment(redisKey, 1L);
        redisTemplateForSession.expire(redisKey, 1, TimeUnit.HOURS);
        return errorNum.intValue();
    }

    private Integer getLoginErrorNum(String uId) {
        String redisKey = loginErrorNumRedisKey(uId);
        if (redisTemplateForSession.hasKey(redisKey)) {
            return redisTemplateForSession.opsForValue().increment(redisKey, 0).intValue();
        } else {
            return 0;
        }
    }

    /**
     * 登录失败次数，在redis中的key
     *
     * @param uId
     * @return
     */
    private String loginErrorNumRedisKey(String uId) {
        return "web_login_error_num_key_" + uId;
    }

    /**
     * 从httprequest 中 解析登录用户的唯一标识（未登录时，默认使用用户名）
     *
     * @param request
     * @return
     */
    public String loginUserUid(HttpServletRequest request) {
        return Hashing.sha256().
                hashString(
                        HttpClientUtil.getIpAddress(request) + "_" + request.getParameter(AuthConstant.RequestHeader.USER_AGENT),
                        StandardCharsets.UTF_8).toString();
    }

    /**
     * 查询验证码 from redis
     *
     * @param uId
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getVerificationFromRedis(String uId) {
        String redisKey = verificationRedisKey(uId);
        Object verification = redisTemplateForSession.opsForValue().get(redisKey);
        return verification == null ? null : verification.toString();
    }

    /**
     * 获得验证码在redis 中的key
     *
     * @param uId
     * @return
     */
    public String verificationRedisKey(String uId) {
        return "web_verification_redis_key_" + uId;
    }

    /**
     * 保存验证码到redis 中
     *
     * @param uId
     * @param verification 验证码text
     */
    @SuppressWarnings("unchecked")
    public void saveVerificationToRedis(String uId, String verification) {
        String redisKey = verificationRedisKey(uId);
        redisTemplateForSession.opsForValue().set(redisKey, verification, 5, TimeUnit.MINUTES);
    }

    /**
     * 删除验证码到redis 中
     *
     * @param uId
     */
    @SuppressWarnings("unchecked")
    public void removeVerificationToRedis(String uId) {
        redisTemplateForSession.delete(verificationRedisKey(uId));
    }

    /**
     * 验证是否清空验证码
     *
     * @param uId
     * @return true 刷新  false 不刷新
     */
    @SuppressWarnings("unchecked")
    public Boolean checkRefreshVerification(String uId) {
        try {
            // 获得验证码刷新次数redis key
            String redisKey = "verification_refresh_num_" + uId;
            // 获得验证码刷新次数
            long refreshNum = redisTemplateForSession.opsForValue().increment(redisKey, 1);
            redisTemplateForSession.expire(redisKey, 10, TimeUnit.MINUTES);
            if (refreshNum > RandomUtil.getRandomInt(VERIFICATION_KEEP_NUM)) {
                redisTemplateForSession.delete(redisKey);
                return true;
            }
        } catch (Exception ex) {
            return true;
        }
        return false;
    }

    /**
     * 密码加密器
     *
     * @return
     */
    @Override
    public PasswordEncoder passwordEncoderBuilder() {
        return customPasswordEncoder;
    }

    /**
     * 按登录表单用户名查询用户
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userAuthService.loadUserByName(userName);
    }

    /**
     * 自定义验证权限选择器
     *
     * @return
     */
    @Override
    public List<AccessDecisionVoter<?>> customAccessDecisionVoter() {
        return null;
    }

    /**
     * 提供权限项与角色关系数据
     *
     * @return
     */
    @Override
    public List<CustomerSecurityConfigAttributes> loadPermissionData() {
        return userAuthService.loadPermissionData();
    }

    /**
     * 按用户登录token 查询用户信息
     *
     * @param servletRequest
     * @param userToken
     * @return
     */
    @Override
    public Authentication loadUserInfoByToken(ServletRequest servletRequest, String userToken) {
        // 通过userToken生成
        String redisToken = userAuthService.getRedisTokenStr((HttpServletRequest) servletRequest, userToken);
        // 从缓存中解析用户id并查询用户数据
        UserDTO userDTO = userAuthService.loadUserFromSession(redisToken);
        // 组合用户数据到authentication中
        return userAuthService.groupAuthenticationFromUser(userDTO);
    }

    /**
     * 按用户登录token 解析用户在redis中保存的token
     *
     * @param httpServletRequest
     * @param tokenHeader
     * @return
     */
    @Override
    public String parseLoginToken(HttpServletRequest httpServletRequest, String tokenHeader) {
        String userTokenKey = loginToken();
        String userToken = httpServletRequest.getHeader(userTokenKey);
        if (Strings.isNullOrEmpty(userToken)) {
            userToken = httpServletRequest.getParameter(userTokenKey.toLowerCase());
        }
        return userToken;
    }
}
