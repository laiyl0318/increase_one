package com.jiayi.model.request.sys;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author wyq
 * @date 2020-11-18
 */
@Data
public class UserPwdRequest implements Serializable {

    public static final String PASSWORD = "新密码";
    public static final String CONFIRM_PASSWORD = "确认密码";
    private static final long serialVersionUID = -25188817288084863L;
    @Size(groups = {UserPwdUpdateProfile.class}, max = 20, min = 8, message = "请输入 8-20 位密码")
    @NotBlank(groups = {UserPwdUpdateProfile.class}, message = "请输入" + PASSWORD)
    private String password;

    @Size(groups = {UserPwdUpdateProfile.class}, max = 20, min = 8, message = "请输入 8-20 位密码")
    @NotBlank(groups = {UserPwdUpdateProfile.class}, message = "请输入" + CONFIRM_PASSWORD)
    private String confirmPassword;

    public interface UserPwdUpdateProfile {
    }
}
