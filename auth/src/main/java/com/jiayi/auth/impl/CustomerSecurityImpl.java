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
     * ?????????????????????????????????
     */
    public static final Integer USE_VERIFICATION_ERROR_NUM = 2;
    /**
     * ??????????????????????????????
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
     * ?????????????????????????????????url
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
     * ????????????token???key
     *
     * @return
     */
    @Override
    public String loginToken() {
        return AuthConstant.RequestHeader.AUTHORIZATION;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @Override
    public String loginUrl() {
        return "/api/v1/user/login";
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @Override
    public String logoutUrl() {
        return "/api/v1/user/logout";
    }

    /**
     * ?????????????????????
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
            response = ResponseUtil.failure(CommonResultEnum.BUSINESS_FAILED, "????????????????????????");
        }

        String uId = loginUserUid(httpServletRequest);
        // ???????????????????????????
        if (checkRefreshVerification(uId)) {
            removeVerificationToRedis(uId);
        }
        // ??????????????????????????????
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
            throw new BusinessException("????????????");
        }
        return new HandlerResponse(HttpStatus.OK, result);
    }

    /**
     * ?????????????????????
     *
     * @param httpServletRequest
     * @param authentication
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public HandlerResponse loginSuccessHandler(HttpServletRequest httpServletRequest, Authentication authentication) {
        try {
            // ?????????????????????????????????session
            CustomUserDetails userDetails = ((CustomUserDetails) authentication.getPrincipal());
            // ??????????????????????????????????????????
            if (!userAuthService.userHasRoles(userDetails.getUserId()).isSuccess()) {
                return new HandlerResponse(HttpStatus.OK, objectMapper.writeValueAsString(ResponseUtil.failure("????????????????????????")));
            }
            // ??????usertoken
            UserTokenModel userToken = new UserTokenModel(userDetails.getUsername(), userDetails.getPassword(),
                    HttpClientUtil.getIpAddress(httpServletRequest), httpServletRequest.getHeader(AuthConstant.RequestHeader.USER_AGENT));
            // ??????userToken???????????????????????????
            String userTokenStr = userToken.generateUserToken();
            // ????????????redis token str??????
            String userRedisTokenStr = userToken.generateRedisToken();

            removeLoginErrorNum(loginUserUid(httpServletRequest));

            redisTemplateForSession.opsForValue().set(userRedisTokenStr, userAuthService.generateSessionValue(userDetails.getUserId().toString()), AuthConstant.LoginState.TIME, TimeUnit.SECONDS);
            return new HandlerResponse(HttpStatus.OK, objectMapper.writeValueAsString(ResponseUtil.success(ImmutableMap.of("token", userTokenStr))));
        } catch (Exception ex) {
            log.error("loginSuccessHandler error", ex);
            throw new BusinessException("??????????????????");
        }

    }


    /**
     * ????????????????????????
     *
     * @param uId
     */
    @SuppressWarnings("unchecked")
    private void removeLoginErrorNum(String uId) {
        redisTemplateForSession.delete(loginErrorNumRedisKey(uId));
        removeVerificationToRedis(uId);
    }

    /**
     * ????????????????????????
     *
     * @param e
     * @return
     */
    @Override
    public HandlerResponse noLoginDeniedHandler(AuthenticationException e) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED_UNAUTHORIZED, "?????????")));
        } catch (Exception ex) {
            log.error("noLoginDeniedHandler error", ex);
            throw new BusinessException("??????????????????");

        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param e
     * @return
     */
    @Override
    public HandlerResponse haveLoginDeniedHandler(AccessDeniedException e) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED_FORBIDDEN, "?????????")));
        } catch (Exception ex) {
            log.error("haveLoginDeniedHandler error", ex);
            throw new BusinessException("??????????????????");

        }
    }

    /**
     * ?????????????????????
     *
     * @param authentication
     * @return
     */
    @Override
    public HandlerResponse logoutSuccessHandler(Authentication authentication) {
        try {
            return new HandlerResponse(HttpStatus.OK,
                    objectMapper.writeValueAsString(ResponseUtil.success("???????????????", null)));
        } catch (Exception ex) {
            log.error("logoutSuccessHandler error", ex);
            throw new BusinessException("??????????????????");

        }
    }

    /**
     * ???????????????
     *
     * @param httpServletRequest
     * @param authentication
     */
    @SuppressWarnings("unchecked")
    @Override
    public void logoutHandler(HttpServletRequest httpServletRequest, Authentication authentication) {
        String userToken = parseLoginToken(httpServletRequest, null);
        String redisToken = userAuthService.getRedisTokenStr(httpServletRequest, userToken);
        // ??????redis ??????
        try {
            if (redisTemplateForSession.hasKey(redisToken)) {
                redisTemplateForSession.delete(redisToken);
            }
        } catch (Exception ex) {
            log.error("???????????????????????????", ex);
        }
    }

    /**
     * ?????????????????????
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    public Boolean useVerification(HttpServletRequest httpServletRequest) {
        return getLoginErrorNum(loginUserUid(httpServletRequest)).compareTo(USE_VERIFICATION_ERROR_NUM) >= 0;
    }

    /**
     * ??????????????????
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
     * ??????????????????????????????
     *
     * @param uId
     * @return ????????????
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
     * ????????????????????????redis??????key
     *
     * @param uId
     * @return
     */
    private String loginErrorNumRedisKey(String uId) {
        return "web_login_error_num_key_" + uId;
    }

    /**
     * ???httprequest ??? ???????????????????????????????????????????????????????????????????????????
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
     * ??????????????? from redis
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
     * ??????????????????redis ??????key
     *
     * @param uId
     * @return
     */
    public String verificationRedisKey(String uId) {
        return "web_verification_redis_key_" + uId;
    }

    /**
     * ??????????????????redis ???
     *
     * @param uId
     * @param verification ?????????text
     */
    @SuppressWarnings("unchecked")
    public void saveVerificationToRedis(String uId, String verification) {
        String redisKey = verificationRedisKey(uId);
        redisTemplateForSession.opsForValue().set(redisKey, verification, 5, TimeUnit.MINUTES);
    }

    /**
     * ??????????????????redis ???
     *
     * @param uId
     */
    @SuppressWarnings("unchecked")
    public void removeVerificationToRedis(String uId) {
        redisTemplateForSession.delete(verificationRedisKey(uId));
    }

    /**
     * ???????????????????????????
     *
     * @param uId
     * @return true ??????  false ?????????
     */
    @SuppressWarnings("unchecked")
    public Boolean checkRefreshVerification(String uId) {
        try {
            // ???????????????????????????redis key
            String redisKey = "verification_refresh_num_" + uId;
            // ???????????????????????????
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
     * ???????????????
     *
     * @return
     */
    @Override
    public PasswordEncoder passwordEncoderBuilder() {
        return customPasswordEncoder;
    }

    /**
     * ????????????????????????????????????
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
     * ??????????????????????????????
     *
     * @return
     */
    @Override
    public List<AccessDecisionVoter<?>> customAccessDecisionVoter() {
        return null;
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Override
    public List<CustomerSecurityConfigAttributes> loadPermissionData() {
        return userAuthService.loadPermissionData();
    }

    /**
     * ???????????????token ??????????????????
     *
     * @param servletRequest
     * @param userToken
     * @return
     */
    @Override
    public Authentication loadUserInfoByToken(ServletRequest servletRequest, String userToken) {
        // ??????userToken??????
        String redisToken = userAuthService.getRedisTokenStr((HttpServletRequest) servletRequest, userToken);
        // ????????????????????????id?????????????????????
        UserDTO userDTO = userAuthService.loadUserFromSession(redisToken);
        // ?????????????????????authentication???
        return userAuthService.groupAuthenticationFromUser(userDTO);
    }

    /**
     * ???????????????token ???????????????redis????????????token
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
