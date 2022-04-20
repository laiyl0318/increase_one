package com.jiayi.model.dto.sys;

import lombok.Data;

import java.util.Set;

/**
 * 权限项传输数据对象 包含角色code list
 *
 * @author cjw
 * @date 2020-10-26
 */
@Data
public class ItemRoleDTO extends ItemDTO {
    private static final long serialVersionUID = 1978163117717498374L;

    private Set<String> roleCodeList;
}
