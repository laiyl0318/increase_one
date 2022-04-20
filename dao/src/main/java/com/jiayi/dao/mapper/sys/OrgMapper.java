package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.OrgDO;

import java.util.List;

public interface OrgMapper {
    /**
     * 保存组织架构信息
     *
     * @param record OrgDO
     * @return int
     */
    int saveOrg(OrgDO record);

    /**
     * 更新组织架构信息
     *
     * @param record OrgDO
     * @return int
     */
    int editOrg(OrgDO record);

    /**
     * 按主键查询组织架构
     *
     * @param orgId
     * @return
     */
    OrgDO selectByPrimaryKey(Integer orgId);

    /**
     * 组织架构列表
     *
     * @param query OrgDO.Query
     * @return List<OrgDO>
     */
    List<OrgDO> orgList(OrgDO.Query query);
}