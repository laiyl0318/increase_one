package com.jiayi.auth.impl.service;

import com.jiayi.auth.impl.CustomerSecurityImpl;
import com.jiayi.auth.impl.constant.AuthConstant;
import com.jiayi.auth.impl.model.CustomAuthenticationToken;
import com.jiayi.auth.impl.model.CustomUserDetails;
import com.jiayi.auth.impl.model.UserTokenModel;
import com.jiayi.auth.security.component.CustomAccessDecisionVoter;
import com.jiayi.auth.security.component.CustomSecurityMetadataSource;
import com.jiayi.auth.security.model.CustomerSecurityConfigAttributes;
import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.AesUtil;
import com.jiayi.common.util.HttpClientUtil;
import com.jiayi.common.util.constants.CommonConstants;
import com.jiayi.domain.sys.UserDomain;
import com.jiayi.model.dto.sys.ItemDTO;
import com.jiayi.model.dto.sys.ItemRoleDTO;
import com.jiayi.model.dto.sys.UserDTO;
import com.jiayi.model.enums.CommonResultEnum;
import com.jiayi.model.enums.ItemEnum;
import com.jiayi.model.request.open.UserCheckAccessRequest;
import com.jiayi.model.response.sys.UserInfoResponse;
import com.jiayi.model.support.ResponseUtil;
import com.jiayi.model.support.response.SysResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cjw
 * @date 2020-10-20
 */
@Slf4j
@Service
public class UserAuthService {
    @Resource
    private UserDomain userDomain;

    @Value("${server.check-permission:true}")
    private Boolean checkPermission;

    @Resource
    private RedisTemplate<String, String> redisTemplateForData;

    @Resource
    private RedisTemplate<String, String> redisTemplateForSession;

    @Resource
    private RedisTemplate<String, String> redisTemplateForPermission;

    @Resource
    private CustomerSecurityImpl customerSecurity;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * ?????????????????????DTO??????
     *
     * @return
     */
    public static UserDTO getUserAndRole() {
        return ((CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserDTO();
    }

    /**
     * ??????????????????????????????????????????UserDetails
     *
     * @param username
     * @return
     */
    public UserDetails loadUserByName(String username) throws UsernameNotFoundException {
        CallResult<UserDTO> userDTOCallResult = userDomain.loadUserByName(username);
        if (!userDTOCallResult.isSuccess()) {
            throw new UsernameNotFoundException(userDTOCallResult.getMsg());
        }
        UserDTO userDTO = userDTOCallResult.getResultObject();
        CustomUserDetails userDetails = new CustomUserDetails(userDTO.getUserName(), userDTO.getPassword(), true, true,
                true, true, Sets.newHashSet(new SimpleGrantedAuthority(CustomUserDetails.ROLE_USER)),
                userDTO.getUserId()
        );
        return userDetails;
    }

    /**
     * redisKey ????????????
     *
     * @param userId
     * @return
     */
    public String redisKeyForUserInfo(Integer userId) {
        return "userinfo_for_id_" + generateMd5HashValue(userId.toString());
    }

    /**
     * redisKey ???????????? ??????????????????????????????
     *
     * @return
     */
    public String redisKeyForItemRole() {
        return "permission_item_role";
    }

    /**
     * ?????????id???????????????????????????????????????
     *
     * @param userId
     * @param platform ????????????
     * @return
     */
    @SuppressWarnings("unchecked")
    public UserDTO loadUserRoleById(Integer userId, String platform) {
        // ???????????????redis???key???
        String redisKeyForUserInfo = redisKeyForUserInfo(userId);
        // ?????????????????????????????????????????????
        try {
            if (redisTemplateForData.hasKey(redisKeyForUserInfo)) {
                ValueOperations<String, String> operations = redisTemplateForData.opsForValue();
                UserDTO userDTO = objectMapper.readValue(operations.get(redisKeyForUserInfo), UserDTO.class);
                if (!Objects.isNull(userDTO)) {
                    userDTO.setPlatform(platform);
                }
                return userDTO;
            }
        } catch (Exception ex) {
            log.error("????????????????????????", ex);
            return null;
        }

        // ?????????????????????????????????????????????
        CallResult<UserDTO> callResult = userDomain.loadUserAndRoleById(userId);
        if (!callResult.isSuccess()) {
            log.warn("?????????????????????????????????" + callResult.getMsg());
        }

        UserDTO userDTO = callResult.isSuccess() ? callResult.getResultObject() : null;
        try {
            // ????????????????????????????????????
            redisTemplateForData.opsForValue().set(redisKeyForUserInfo,
                    objectMapper.writeValueAsString(userDTO), 5, TimeUnit.MINUTES);
        } catch (Exception ex) {
            log.error("?????????????????????????????????", ex);
        }
        if (!Objects.isNull(userDTO)) {
            userDTO.setPlatform(platform);
        }
        return userDTO;
    }

    /**
     * ??????AbstractAuthenticationToken ??? UserDTO
     *
     * @param userDTO
     * @return
     */
    public AbstractAuthenticationToken groupAuthenticationFromUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                CollectionUtils.isEmpty(userDTO.getRoleCodes()) ? null
                        : userDTO.getRoleCodes().stream().map(roleCode -> new SimpleGrantedAuthority(AuthConstant.SECURITY_ROLE_PREFIX + roleCode)).collect(Collectors.toList()));
        authToken.setUserName(userDTO.getUserName());
        authToken.setPassword(userDTO.getPassword());
        authToken.setUserDTO(userDTO);
        authToken.setAuthenticated(true);
        return authToken;
    }

    /**
     * ????????????sessionValue
     *
     * @param sourceStr
     * @return
     */
    public String generateSessionValue(String sourceStr) {
        return generateMd5HashValue(sourceStr) + "_" + sourceStr;
    }

    /**
     * ??????md5 hash???
     *
     * @param sourceStr
     * @return
     */
    private String generateMd5HashValue(String sourceStr) {
        return DigestUtils.md5DigestAsHex(
                (DigestUtils.md5DigestAsHex((sourceStr + CommonConstants.SESSION_SALT).getBytes()) + sourceStr).getBytes());
    }

    /**
     * ????????????session??????
     *
     * @param sessionStr
     * @return
     */
    public Integer parseSessionValue(String sessionStr) {
        if (Strings.isNullOrEmpty(sessionStr)) {
            return null;
        }
        try {
            int intervalIndex = sessionStr.indexOf("_");
            int lastIndex = sessionStr.lastIndexOf("_");
            String part1 = sessionStr.substring(0, intervalIndex);
            String part2;
            if (intervalIndex == lastIndex) {
                part2 = sessionStr.substring(intervalIndex + 1);
            } else {
                part2 = sessionStr.substring(intervalIndex + 1, lastIndex);
            }
            if (part1.equals(generateMd5HashValue(part2))) {
                return Integer.parseInt(part2);
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * ??????header token ??? request ip agent ?????? redisTokenKey
     *
     * @param request
     * @param userToken
     * @return
     */
    public String getRedisTokenStr(HttpServletRequest request, String userToken) {
        // ??????request?????????????????????redistoken; agent ????????????140?????????
        String userAgent = getFormatAgent(request);
        return getRedisTokenFromUserToken(userToken, HttpClientUtil.getIpAddress(request), userAgent);
    }

    /**
     * ??????header token ??? request ip,???????????????agent ?????? redisTokenKey
     *
     * @param request
     * @param userToken
     * @return
     */
    public String getRedisTokenStr(HttpServletRequest request, String userToken,String oldAgent) {
        // ??????request?????????????????????redistoken; agent ????????????140?????????
        if (oldAgent.contains("WeChat") || oldAgent.contains("Weixin")|| oldAgent.contains("wechatdevtools")) {
            oldAgent = oldAgent.substring(0, 140);
        }
        return getRedisTokenFromUserToken(userToken, HttpClientUtil.getIpAddress(request), oldAgent);
    }

    /**
     * ??????????????????agent??? ??????????????????????????????140???????????????, ??????????????????????????????????????????agent????????????(?????????????????????????????????)
     * @param request
     * @return
     */
    private String getFormatAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(AuthConstant.RequestHeader.USER_AGENT);
        if (userAgent.contains("WeChat") || userAgent.contains("Weixin")|| userAgent.contains("wechatdevtools")) {
            userAgent = userAgent.substring(0, 140);
        }
        return userAgent;
    }

    private String getRedisTokenFromUserToken(String userToken, String ip, String userAgent) {
        UserTokenModel tokenModel = new UserTokenModel();
        tokenModel.setToken(userToken);
        tokenModel.setIp(ip);
        tokenModel.setAgent(userAgent);
        String redisToken = tokenModel.generateRedisToken();
        return redisToken;
    }

    /**
     * ???????????????item?????????????????????????????????
     */
    @SuppressWarnings("unchecked")
    public List<CustomerSecurityConfigAttributes> loadPermissionData() {

        List<CustomerSecurityConfigAttributes> result = Lists.newArrayList();
        // ?????????????????????????????????????????????
        if (!checkPermission) {
            CustomerSecurityConfigAttributes attributes = new CustomerSecurityConfigAttributes();
            attributes.setUri("/**");
            attributes.setAttributes(null);
            result.add(attributes);
            return result;
        }

        // redis key  ???????????????
        String redisKeyForItemRole = redisKeyForItemRole();
        List<ItemRoleDTO> itemRoleDTOList = null;
        try {
            if (redisTemplateForPermission.hasKey(redisKeyForItemRole)) {
                itemRoleDTOList = objectMapper.readValue(
                        redisTemplateForPermission.opsForValue().get(redisKeyForItemRole),
                        new TypeReference<List<ItemRoleDTO>>() {
                        });
            } else {
                CallResult<List<ItemRoleDTO>> itemRoleResult = userDomain.loadPermissionItemForRole();
                if (itemRoleResult.isSuccess()) {
                    itemRoleDTOList = itemRoleResult.getResultObject();
                    redisTemplateForPermission.opsForValue().set(redisKeyForItemRole,
                            objectMapper.writeValueAsString(itemRoleDTOList), 10, TimeUnit.MINUTES);
                }
            }
        } catch (Exception ex) {
            log.error("loadPermissionData ??????????????????", ex);
        }
        if (CollectionUtils.isEmpty(itemRoleDTOList)) {
            return result;
        }

        itemRoleDTOList.forEach(itemRoleDTO -> {
            CustomerSecurityConfigAttributes attributes = new CustomerSecurityConfigAttributes();
            attributes.setUri(itemRoleDTO.getItemName());
            if (ItemEnum.NO_LOGIN_ALLOWED.equals(itemRoleDTO.getAllowed())
                    || ItemEnum.LOGIN_ALLOWED.equals(itemRoleDTO.getAllowed())) {
                attributes.setAttributes(null);
            } else {
                if (CollectionUtils.isEmpty(itemRoleDTO.getRoleCodeList())) {
                    attributes.setAttributes(Sets.newHashSet(new SecurityConfig(AuthConstant.SECURITY_NO_PERMISSION_ROLE)));
                } else {
                    attributes.setAttributes(itemRoleDTO.getRoleCodeList()
                            .stream().map(roleCode -> new SecurityConfig(AuthConstant.SECURITY_ROLE_PREFIX + roleCode)).collect(Collectors.toSet()));
                }
            }
            result.add(attributes);
        });

        return result;
    }

    /**
     * ???????????????spring security???url??????
     */
    public List<String> loadNoPmCheckUrls() {
        CallResult<List<ItemDTO>> itemResult = userDomain.loadNoPmCheckItem();
        if (!itemResult.isSuccess()) {
            return Lists.newArrayList();
        }
        return itemResult.getResultObject().stream().map(ItemDTO::getItemName).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     *
     * @param redisToken
     * @return
     */
    @SuppressWarnings("unchecked")
    public UserDTO loadUserFromSession(String redisToken) {
        if (Strings.isNullOrEmpty(redisToken)) {
            return null;
        }
        try {
            if (!redisTemplateForSession.hasKey(redisToken)) {
                return null;
            }
            String userValue = redisTemplateForSession.opsForValue().get(redisToken);
            // ????????????id
            Integer userId = parseSessionValue(userValue);
            if (userId == null) {
                return null;
            }
            // ??????redis???????????????
            redisTemplateForSession.expire(redisToken, AuthConstant.LoginState.TIME, TimeUnit.SECONDS);
            // ??????????????????,????????????????????????
            String platform = AuthConstant.PLATFORM_WEB;
            if (userValue.contains(AuthConstant.PLATFORM_WECHAT)) {
                platform = AuthConstant.PLATFORM_WECHAT;
            }
            return loadUserRoleById(userId, platform);
        } catch (Exception ex) {
            log.error("????????????????????????", ex);
            return null;
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param redisKeyForUserInfo
     * @return
     */
    public void removeUserFromSession(String redisKeyForUserInfo) {
        if (redisTemplateForData.hasKey(redisKeyForUserInfo)) {
            redisTemplateForData.delete(redisKeyForUserInfo);
            log.info("???????????????????????????????????? redisToken={}", redisKeyForUserInfo);
        }
    }

    /**
     * ??????token ??????????????????????????????url??????
     *
     * @return
     */
    public SysResponse checkCanAccess(UserCheckAccessRequest form) {
        UserDTO userDTO = loadUserFromSession(getRedisTokenFromUserToken(form.getToken(), form.getHost(), form.getUserAgent()));
        if (userDTO == null) {
            return ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED);
        }
        FilterInvocation fi = new FilterInvocation(form.getUri(), null);
        CustomSecurityMetadataSource customSecurityMetadataSource = new CustomSecurityMetadataSource(customerSecurity);
        // ??????url ?????????????????????
        Collection<ConfigAttribute> attributes = customSecurityMetadataSource.getAttributes(fi);
        // ??????????????????,????????????????????????
        if (CollectionUtils.isEmpty(attributes)) {
            return ResponseUtil.success("????????????",
                    new UserInfoResponse(AesUtil.encryptUrlSafe(userDTO.getUserId().toString()), userDTO.getNickName(),
                            userDTO.getRoleCodes()));
        }
        // ???????????????auth
        CustomAccessDecisionVoter customAccessDecisionVoter = new CustomAccessDecisionVoter();
        int result = customAccessDecisionVoter.vote(groupAuthenticationFromUser(userDTO), fi, attributes);
        if (AccessDecisionVoter.ACCESS_GRANTED == result) {
            return ResponseUtil.success("????????????",
                    new UserInfoResponse(AesUtil.encryptUrlSafe(userDTO.getUserId().toString()), userDTO.getNickName(),
                            userDTO.getRoleCodes()));
        } else {
            return ResponseUtil.failure(CommonResultEnum.ACCESS_DENIED);
        }
    }

    /**
     * ???????????????????????????
     * @param userId
     * @return
     */
    public CallResult<String> userHasRoles(Integer userId) {
        if (userDomain.loadUserRoles(userId).size() > 0) {
            return CallResult.success();
        } else {
            return CallResult.failure();
        }
    }

}
