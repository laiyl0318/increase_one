package com.jiayi.model.request.sys;

import com.jiayi.common.util.AesUtil;
import com.jiayi.model.support.request.AbstractValidation;
import com.jiayi.model.support.request.profiles.AddProfile;
import com.jiayi.model.support.request.profiles.DeleteProfile;
import com.jiayi.model.support.request.profiles.EditProfile;
import com.google.common.base.Strings;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author cjw
 * @date 2020-10-20
 */
@Data
public class OrgRequest extends AbstractValidation {
    private static final long serialVersionUID = 8414407555111779811L;
    /**
     * 组织架构ID
     */
    private static final String MSG_ORG_ID = MSG_TITLE + "组织架构编号";

    /**
     * 组织架构名称
     */
    private static final String MSG_ORG_NAME = MSG_TITLE + "组织架构名称";
    /**
     * 上级ID
     */
    private static final String MSG_PARENT = MSG_TITLE + "上级CODE";

    @NotNull(groups = {EditProfile.class, DeleteProfile.class}, message = MSG_ORG_ID + "不能为空")
    @NotBlank(groups = {EditProfile.class, DeleteProfile.class}, message = MSG_ORG_ID)
    private String orgId;

    @Size(groups = {AddProfile.class, EditProfile.class}, max = 15, min = 2, message = MSG_ORG_NAME + "格式错误（支持长度: 2-15字符）")
    @NotBlank(groups = {AddProfile.class, EditProfile.class}, message = MSG_ORG_NAME + "不能为空")
    @NotNull(groups = {AddProfile.class, EditProfile.class}, message = MSG_ORG_NAME)
    private String orgName;

    @NotBlank(groups = {AddProfile.class}, message = MSG_PARENT + "不能为空")
    @NotNull(groups = {AddProfile.class}, message = MSG_PARENT)
    private String parentId;

    /**
     * 解密组织架构ID
     *
     * @return Integer
     */
    public Integer getOrgId() {
        if (Strings.isNullOrEmpty(orgId)) {
            return null;
        }
        return Integer.parseInt(AesUtil.decryptUrlSafe(orgId));
    }

    /**
     * 解密父级ID
     *
     * @return Integer
     */
    public Integer getParentId() {
        if (Strings.isNullOrEmpty(parentId)) {
            return null;
        }
        return Integer.parseInt(AesUtil.decryptUrlSafe(parentId));
    }
}
