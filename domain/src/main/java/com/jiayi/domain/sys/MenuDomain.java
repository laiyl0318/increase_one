package com.jiayi.domain.sys;

import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.BeanCopierUtil;
import com.jiayi.dao.data.sys.MenuDO;
import com.jiayi.dao.data.sys.MenuTaskDO;
import com.jiayi.dao.data.sys.RoleTaskDO;
import com.jiayi.dao.data.sys.UserRoleDO;
import com.jiayi.dao.mapper.sys.MenuMapper;
import com.jiayi.dao.mapper.sys.MenuTaskMapper;
import com.jiayi.dao.mapper.sys.RoleTaskMapper;
import com.jiayi.dao.mapper.sys.UserRoleMapper;
import com.jiayi.model.dto.sys.MenuDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author laiyilong
 */
@Service
public class MenuDomain {
    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleTaskMapper roleTaskMapper;

    @Resource
    private MenuTaskMapper menuTaskMapper;

    @Resource
    private MenuMapper menuMapper;

    /**
     * 查询用户拥有权限的菜单列表
     *
     * @return
     */
    public CallResult<List<MenuDTO>> listForUser(Integer userId) {
        // 查询用户角色
        List<UserRoleDO> userRoles = userRoleMapper.userRoleList(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return CallResult.failure("用户没有角色数据");
        }
        // 查询角色关联任务
        RoleTaskDO.Query roleTaskQuery = new RoleTaskDO.Query();
        roleTaskQuery.setRoleCodes(userRoles.stream().map(UserRoleDO::getRoleCode).collect(Collectors.toList()));
        List<RoleTaskDO> roleTasks = roleTaskMapper.roleTaskList(roleTaskQuery);
        if (CollectionUtils.isEmpty(roleTasks)) {
            return CallResult.failure("用户没有权限");
        }
        // 通过任务过滤出菜单code
        MenuTaskDO.Query menuTaskQuery = new MenuTaskDO.Query();
        menuTaskQuery.setTaskCodes(roleTasks.stream().map(RoleTaskDO::getTaskCode).collect(Collectors.toList()));
        List<MenuTaskDO> menuTasks = menuTaskMapper.listByQuery(menuTaskQuery);
        if (CollectionUtils.isEmpty(menuTasks)) {
            return CallResult.failure("用户没有菜单数据");
        }
        // 定义过滤menuCodes
        Set<String> menuCodesFilter = menuTasks.stream().map(MenuTaskDO::getMenuCode).collect(Collectors.toSet());
        // 查询全量菜单数据
        List<MenuDO> menus = menuMapper.listMenu(new MenuDO.Query());
        if (CollectionUtils.isEmpty(menus)) {
            return CallResult.failure("用户没有菜单数据");
        }
        // 返回过滤后的菜单数据
        List<MenuDTO> menuDTOS = menus.stream()
                .filter(menuDO -> menuCodesFilter.contains(menuDO.getMenuCode()))
                .map(menuDO -> BeanCopierUtil.copy(MenuDTO.class, menuDO))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(menuDTOS)) {
            return CallResult.failure("用户没有菜单数据");
        }
        return CallResult.success(menuDTOS);
    }
}
