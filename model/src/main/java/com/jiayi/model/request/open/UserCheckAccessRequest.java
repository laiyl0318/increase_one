package com.jiayi.model.request.open;

import com.jiayi.model.support.request.BaseSignRequest;
import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

/**
 * @author cjw
 * @date 2020-10-29
 */
@Data
public class UserCheckAccessRequest extends BaseSignRequest {
    private static final long serialVersionUID = 3959413354968037045L;

    /**
     * 登录用户token
     */
    @NotBlank(groups = UserAccessCheckGroup.class, message = "登录用户token不可为空")
    private String token;

    /**
     * 登录用户主机ip
     */
    @NotBlank(groups = UserAccessCheckGroup.class, message = "登录用户host不可为空")
    private String host;

    /**
     * 登录用户浏览器标识User-Agent
     */
    @NotBlank(groups = UserAccessCheckGroup.class, message = "登录用户浏览器标识不可为空")
    private String userAgent;

    /**
     * 登录用户请求地址
     */
    @NotBlank(groups = UserAccessCheckGroup.class, message = "验证请求地址不可为空")
    private String uri;

    /**
     * 验证场景：用户请求接口权限参数验证
     */
    public interface UserAccessCheckGroup {

    }

    /**
     * 验证场景组：签名参数验证、用户请求接口权限参数验证
     */
    @GroupSequence({CheckSignProfile.class, UserAccessCheckGroup.class})
    public interface UserAccessCheckGroups {

    }
}
