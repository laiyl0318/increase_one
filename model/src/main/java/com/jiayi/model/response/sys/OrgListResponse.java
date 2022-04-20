package com.jiayi.model.response.sys;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 组织架构列表输出模型
 *
 * @author wyq
 * @date 2020/10/14
 */
@Data
public class OrgListResponse implements Serializable {

    private static final long serialVersionUID = 1462782623173437349L;

    private String orgId;

    private String orgCode;

    private String orgName;

    private Integer parentId;

    private Byte level;

    private List<OrgListResponse> child;
}
