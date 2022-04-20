package com.jiayi.model.request.sys;

import com.jiayi.model.support.request.BasePaginationValidation;
import com.jiayi.model.support.request.profiles.QueryListProfile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author wyq
 * @date 2020-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleQueryRequest extends BasePaginationValidation {
    public static final String FIELD_ROLE_NAME = "角色名称";
    private static final long serialVersionUID = 311796277898019352L;
    @Size(groups = {QueryListProfile.class}, max = 50, message = FIELD_ROLE_NAME + "不能超过{max}个字符")
    @Pattern(groups = {QueryListProfile.class}, regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z]*$", message = FIELD_ROLE_NAME + "不能出现特殊字符")
    private String roleName;
}
