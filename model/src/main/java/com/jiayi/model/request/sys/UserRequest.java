package com.jiayi.model.request.sys;

import com.jiayi.common.checks.CheckAes;
import com.jiayi.common.util.AesUtil;
import com.jiayi.model.support.request.BasePaginationValidation;
import com.jiayi.model.support.request.profiles.AddProfile;
import com.jiayi.model.support.request.profiles.EditProfile;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author wyq
 * @date 2020-08-20
 */
@Data
public class UserRequest extends BasePaginationValidation {


    @CheckAes(groups = {EditProfile.class, UnbindWechatProfile.class}, message = "请传入正确用户id")
    @NotNull(groups = {EditProfile.class, UnbindWechatProfile.class}, message = "请传入用户id")
    private String userId;

    @CheckAes(groups = {AddProfile.class}, message = "请选择正确角色")
    private List<String> roleCodes;

    @NotNull(groups = AddProfile.class, message = "请输入用户邮箱")
    private String email;

    @NotNull(groups = AddProfile.class, message = "请传入用户类型")
    private Byte userType;

    /**
     * 业主用户所属省份code
     */
    private String provinceCode;

    /**
     * 业主用户所属城市code
     */
    private String cityCode;

    /**
     * 业主用户所属区县code
     */
    private String countyCode;

    /**
     * 组织架构id(部门)
     */
    @CheckAes(groups = {AddProfile.class}, ignoreEmpty = true, message = "请选择正确的组织架构")
    private String orgId;

    @Range(groups = {EditProfile.class}, min = 1, max = 3, message = "请正确选择用户状态")
    private Byte userStatus;

    @NotBlank(groups = {AddProfile.class}, message = "请输入用户名")
    private String userName;

    @Size(groups = {AddProfile.class}, max = 20, min = 8, message = "请输入 8-20 位密码")
    @NotBlank(groups = {AddProfile.class}, message = "请输入登录密码")
    private String password;

    @NotBlank(groups = {AddProfile.class}, message = "请输入登录用户名")
    private String realName;

    @NotBlank(groups = {AddProfile.class,GetReportListProfile.class}, message = "请输入手机号")
    private String userMobile;

    /**
     * 搜索关键词
     */
    private String searchKey;

    /**
     * 微信小程序
     * 1、实施助手 2、福瑞莱环保 3、福瑞莱凤巢
     */
    @Range(groups = {AddSubTimesProfile.class,QuerySubTimesProfile.class,UnbindWechatProfile.class}, min = 1, max = 3, message = "请选择正确的小程序")
    @NotNull(groups = {AddSubTimesProfile.class,QuerySubTimesProfile.class,UnbindWechatProfile.class}, message = "请选择小程序")
    private Byte weChatType;

    /**
     * 通知类型
     * 1、审批通知
     */
    @Range(groups = {AddSubTimesProfile.class}, min = 1, max = 1, message = "请选择正确的通知类型")
    @NotNull(groups = {AddSubTimesProfile.class}, message = "请选择通知类型")
    private Byte weChatSubType;

    @NotNull(groups = AuthLoginProfile.class, message = "请传入浏览器信息")
    private String agent;

    /**
     * 角色列表
     * @return
     */
    public List<String> getRoleCodes() {
        if (CollectionUtils.isEmpty(this.roleCodes)) {
            return null;
        }
        List<String> roles = Lists.newArrayList();
        this.roleCodes.forEach(roleIdEncode -> {
            roles.add(AesUtil.decryptUrlSafe(roleIdEncode));
        });
        return roles;
    }

    /**
     * 组织架构
     * @return
     */
    public Integer getOrgId() {
        if (Strings.isNullOrEmpty(orgId)) {
            return null;
        }
        return Integer.parseInt(AesUtil.decryptUrlSafe(orgId));
    }

    public Integer getUserId() {
        if (Strings.isNullOrEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(AesUtil.decryptUrlSafe(userId));
    }

    /**
     * 解除微信用户绑定
     */
    public interface UnbindWechatProfile {
    }

    /**
     * 增加通知次数
     */
    public interface AddSubTimesProfile {
    }

    /**
     * 查询通知次数
     */
    public interface QuerySubTimesProfile {
    }

    /**
     * 授权登录
     */
    public interface AuthLoginProfile {
    }

    /**
     * 获取日报
     */
    public interface GetReportListProfile {
    }
}
