package com.jiayi.domain.sys;

import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.BeanCopierUtil;
import com.jiayi.dao.data.sys.OrgDO;
import com.jiayi.dao.mapper.sys.OrgMapper;
import com.jiayi.model.dto.sys.OrgDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author laiyilong
 */
@Service
@Slf4j
public class OrgDomain {
    public static final Integer LEVEL_BEGIN_CODE = 1000;

    public static final Integer MAX_LEVEL = 5;

    @Resource
    private OrgMapper orgMapper;

    /**
     * 通过组织架构获得数据对象
     *
     * @param orgId Integer
     * @return CallResult<OrgDTO>
     */
    public CallResult<OrgDTO> getOrgById(Integer orgId) {
        OrgDO orgDO = orgMapper.selectByPrimaryKey(orgId);
        if (Objects.isNull(orgDO)) {
            return CallResult.failure("部门-组织架构不存在");
        }
        return CallResult.success(BeanCopierUtil.copy(OrgDTO.class, orgDO));
    }

    /**
     * 组织架构数据列表
     *
     * @return List<OrgDTO>
     */
    public List<OrgDTO> orgList() {
        OrgDO.Query query = new OrgDO.Query();
        List<OrgDO> orgDOS = orgMapper.orgList(query);
        return CollectionUtils.isEmpty(orgDOS) ? Lists.newArrayList() : BeanCopierUtil.copyList(OrgDTO.class, orgDOS);
    }

    /**
     * 组织架构map， code =》 dto
     *
     * @return Map<String, OrgDTO>
     */
    public Map<String, OrgDTO> orgCodeMap() {
        return orgList().stream().collect(Collectors.toMap(OrgDTO::getOrgCode, orgDTO -> orgDTO));
    }

    /**
     * 保存组织架构数据
     *
     * @param orgDTO OrgDTO
     * @return CallResult<Integer>
     */
    public CallResult<Integer> saveOrg(OrgDTO orgDTO) {
        OrgDO orgDO = BeanCopierUtil.copy(OrgDO.class, orgDTO);
        if (orgMapper.saveOrg(orgDO) == 1) {
            return CallResult.success(orgDO.getOrgId());
        }
        return CallResult.failure();
    }

    /**
     * 编辑组织架构信息
     *
     * @param orgUpdateDTO OrgDTO
     * @return CallResult<Integer>
     */
    public CallResult<Integer> editOrg(OrgDTO orgUpdateDTO) {
        OrgDO orgDO = BeanCopierUtil.copy(OrgDO.class, orgUpdateDTO);
        // 更新组织架构
        if (orgMapper.editOrg(orgDO) == 0) {
            return CallResult.failure("编辑组织架构失败");
        }
        return CallResult.success(orgDO.getOrgId());
    }
}
