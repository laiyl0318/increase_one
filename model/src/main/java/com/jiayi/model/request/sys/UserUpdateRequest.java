package com.jiayi.model.request.sys;

import com.jiayi.common.util.AesUtil;
import com.jiayi.model.support.request.BasePaginationValidation;
import com.google.common.base.Strings;
import lombok.Data;

/**
 * @author laiyilong
 * @date 2020-09-02
 */
@Data
public class UserUpdateRequest extends BasePaginationValidation {

    private String roleId;

    private String userId;

    private Byte userType;

    private Byte gender;

    private Byte available;

    private String userName;

    private String realName;

    private String userMobile;

    public Integer getRoleId() {
        if (Strings.isNullOrEmpty(roleId)) {
            return null;
        }
        return Integer.valueOf(AesUtil.decryptUrlSafe(roleId));
    }

    public Integer getUserId() {
        if (Strings.isNullOrEmpty(userId)) {
            return null;
        }
        return Integer.valueOf(AesUtil.decryptUrlSafe(userId));
    }

    public interface UserUpdateProfile {
    }
}
