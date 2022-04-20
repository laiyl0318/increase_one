package com.jiayi.domain.sys;

import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.BeanCopierUtil;
import com.jiayi.dao.data.sys.ItemDO;
import com.jiayi.dao.data.sys.RoleDO;
import com.jiayi.dao.mapper.sys.ItemMapper;
import com.jiayi.dao.mapper.sys.RoleMapper;
import com.jiayi.model.dto.sys.ItemDTO;
import com.jiayi.model.dto.sys.RoleDTO;
import com.jiayi.model.support.PageLimit;
import com.jiayi.model.support.PageModel;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限处理器
 *
 * @author laiyilong
 */
@Service
public class RbacDomain {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private ItemMapper itemMapper;

    /**
     * 角色列表
     *
     * @return
     */
    public List<RoleDTO> roleSelectList() {
        List<RoleDO> roles = roleMapper.roleList(new RoleDO.Query());
        return CollectionUtils.isEmpty(roles) ? Lists.newArrayList() : BeanCopierUtil.copyList(RoleDTO.class, roles);
    }

    /**
     * 角色map
     *
     * @return
     */
    public Map<String, RoleDTO> roleMap() {
        return roleMapper.roleList(new RoleDO.Query()).stream().collect(
                Collectors.toMap(RoleDO::getRoleCode, roleDO -> BeanCopierUtil.copy(RoleDTO.class, roleDO)));
    }

    /**
     * 角色列表(分页)
     *
     * @return
     */
    public CallResult<PageModel<RoleDTO>> roleList(RoleDTO.Query query, PageLimit pageLimit) {
        RoleDO.Query queryDO = BeanCopierUtil.copy(RoleDO.Query.class, query);
        if (pageLimit != null) {
            PageHelper.startPage(pageLimit.getPageNum(), pageLimit.getPageSize());
        }
        List<RoleDO> roleDOS = roleMapper.roleList(queryDO);
        if (CollectionUtils.isEmpty(roleDOS)) {
            return CallResult.failure("没有角色数据");
        }
        // 接收返回分页数据
        if (roleDOS instanceof Page) {
            Page pageResult = (Page) roleDOS;
            return CallResult.success(PageModel.newInstance(BeanCopierUtil.copyList(RoleDTO.class, roleDOS),
                    pageResult.getPageNum(),
                    pageResult.getPageSize(), pageResult.getPages(), pageResult.getTotal()));
        } else {
            return CallResult.success(PageModel.newInstance(BeanCopierUtil.copyList(RoleDTO.class, roleDOS)));
        }
    }

    /**
     * 权限数据列表
     *
     * @return
     */
    public List<ItemDTO> itemList() {
        return BeanCopierUtil.copyList(ItemDTO.class, itemMapper.itemList(new ItemDO.Query()));
    }

    /**
     * 批量保存权限项
     *
     * @param itemDTOS
     * @return
     */
    public CallResult<String> batchAddItem(List<ItemDTO> itemDTOS) {
        if (itemMapper.batchAdd(BeanCopierUtil.copyList(ItemDO.class, itemDTOS)) == 0) {
            return CallResult.failure();
        }
        return CallResult.success();
    }

    /**
     * 批量更新权限项
     *
     * @param itemDTOS
     * @return
     */
    public CallResult<String> batchEditItem(List<ItemDTO> itemDTOS) {
        itemDTOS.forEach(itemDTO -> {
            itemMapper.editItem(BeanCopierUtil.copy(ItemDO.class, itemDTO));
        });
        return CallResult.success();
    }
}
