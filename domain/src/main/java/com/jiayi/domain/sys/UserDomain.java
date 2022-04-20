package com.jiayi.domain.sys;

import com.jiayi.common.model.CallResult;
import com.jiayi.common.util.BeanCopierUtil;
import com.jiayi.dao.data.sys.*;
import com.jiayi.dao.mapper.sys.*;
import com.jiayi.model.dto.sys.ItemDTO;
import com.jiayi.model.dto.sys.ItemRoleDTO;
import com.jiayi.model.dto.sys.UserDTO;
import com.jiayi.model.enums.ItemEnum;
import com.jiayi.model.support.PageLimit;
import com.jiayi.model.support.PageModel;
import com.jiayi.repository.user.UserRepository;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author laiyilong
 */
@Service
public class UserDomain {

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private ItemMapper itemMapper;

    @Resource
    private TaskItemMapper taskItemMapper;

    @Resource
    private RoleTaskMapper roleTaskMapper;

    /**
     * 保存用户数据
     *
     * @param userDTO
     * @return
     */
    public CallResult<Integer> saveUser(UserDTO userDTO) {
        UserDO userDO = BeanCopierUtil.copy(UserDO.class, userDTO);
        if (userMapper.saveUser(userDO) == 1) {
            return CallResult.success(userDO.getUserId());
        }
        return CallResult.failure();
    }

    /**
     * 保存用户角色数据
     *
     * @param userId
     * @param roleCodes
     * @return
     */
    public CallResult<String> saveUserRole(Integer userId, List<String> roleCodes) {
        List<UserRoleDO> userRoleDOS = roleCodes.stream().map(roleCode -> {
            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setUserId(userId);
            userRoleDO.setRoleCode(roleCode);
            userRoleDO.setCreateTime(LocalDateTime.now());
            return userRoleDO;
        }).collect(Collectors.toList());
        if (userRoleMapper.batchSave(userRoleDOS) > 0) {
            return CallResult.success();
        }
        return CallResult.failure("保存用户角色数据失败");
    }

    /**
     * 保存用户数据包含角色
     *
     * @param userDTO
     * @return
     */
    public CallResult<Integer> saveUserWithRole(UserDTO userDTO) {
        CallResult<Integer> saveUserResult = saveUser(userDTO);
        if (!saveUserResult.isSuccess()) {
            return CallResult.failure("保存用户数据失败");
        }
        if (CollectionUtils.isEmpty(userDTO.getRoleCodes())) {
            return CallResult.success(saveUserResult.getResultObject());
        }
        CallResult<String> saveUserRoleResult = saveUserRole(saveUserResult.getResultObject(), userDTO.getRoleCodes());
        if (!saveUserRoleResult.isSuccess()) {
            return CallResult.failure("保存用户角色数据失败");
        }
        return CallResult.success(saveUserResult.getResultObject());
    }

    /**
     * 按登录名查询用户对象数据
     *
     * @param userName
     * @return
     */
    public CallResult<UserDTO> loadUserByName(String userName) {
        UserDO userDO = userRepository.getUserByUserName(userName);
        if (Objects.isNull(userDO)) {
            return CallResult.failure("用户不存在");
        }
        return CallResult.success(BeanCopierUtil.copy(UserDTO.class, userDO));
    }

    /**
     * 按用户手机号查询用户
     *
     * @param userMobile
     * @return
     */
    public CallResult<UserDTO> loadUserByMobile(String userMobile) {
        UserDO userDO = userRepository.getUserByMobile(userMobile);
        if (Objects.isNull(userDO)) {
            return CallResult.failure("用户不存在");
        }
        return CallResult.success(BeanCopierUtil.copy(UserDTO.class, userDO));
    }


    /**
     * 查询用户角色数据
     * @param userId
     * @return
     */
    public List<String> loadUserRoles(Integer userId) {
        return userRoleMapper.userRoleList(userId).stream().map(UserRoleDO::getRoleCode).collect(Collectors.toList());
    }

    /**
     * 查询用户数据+角色数据
     *
     * @param userId
     * @return
     */
    public CallResult<UserDTO> loadUserAndRoleById(Integer userId) {
        CallResult<UserDTO> userResult = userInfo(userId);
        if (!userResult.isSuccess()) {
            return CallResult.failure("用户不存在");
        }
        UserDTO userDTO = userResult.getResultObject();
        // 查询用户角色
        List<UserRoleDO> userRoleDOS = userRoleMapper.userRoleList(userDTO.getUserId());
        if (!CollectionUtils.isEmpty(userRoleDOS)) {
            userDTO.setRoleCodes(userRoleDOS.stream().map(UserRoleDO::getRoleCode).collect(Collectors.toList()));
        }
        return CallResult.success(userDTO);
    }

    /**
     * 查询用户表信息
     *
     * @param userId
     * @return
     */
    public CallResult<UserDTO> userInfo(Integer userId) {
        UserDO userDO = userMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(userDO)) {
            return CallResult.failure("用户不存在");
        }
        return CallResult.success(BeanCopierUtil.copy(UserDTO.class, userDO));
    }

    public CallResult<List<ItemDTO>> loadNoPmCheckItem() {
        // 查询不需要登录、权限验证的item数据
        ItemDO.Query query = new ItemDO.Query();
        query.setAllowed(ItemEnum.NO_LOGIN_ALLOWED.getValue());
        List<ItemDO> items = itemMapper.itemList(query);
        if (CollectionUtils.isEmpty(items)) {
            return CallResult.failure("没有无需登录访问的权限数据");
        }
        return CallResult.success(BeanCopierUtil.copyList(ItemDTO.class, items));
    }

    public CallResult<List<ItemRoleDTO>> loadPermissionItemForRole() {
        // 查询需要验证的权限项
        ItemDO.Query query = new ItemDO.Query();
        query.setAllowedList(Lists.newArrayList(ItemEnum.HAVE_PERMISSION_ALLOWED.getValue(),
                ItemEnum.LOGIN_ALLOWED.getValue()));
        List<ItemDO> items = itemMapper.itemList(query);


        if (CollectionUtils.isEmpty(items)) {
            return CallResult.failure("查询权限项数据为空");
        }
        // 组合itemCode 与 roleCode map
        Map<String, Set<String>> roleTaskMap = Maps.newHashMap();
        // 查询taskItem关系数据
        TaskItemDO.Query taskItemQuery = new TaskItemDO.Query();
        taskItemQuery.setItemCodes(items.stream().map(ItemDO::getItemCode).collect(Collectors.toList()));
        List<TaskItemDO> taskItems = taskItemMapper.taskItemList(taskItemQuery);
        if (!CollectionUtils.isEmpty(taskItems)) {
            // 查询roleTask关系数据
            RoleTaskDO.Query roleTaskQuery = new RoleTaskDO.Query();
            roleTaskQuery.setTaskCodes(taskItems.stream().map(TaskItemDO::getTaskCode).distinct().collect(Collectors.toList()));
            List<RoleTaskDO> roleTasks = roleTaskMapper.roleTaskList(roleTaskQuery);
            if (!CollectionUtils.isEmpty(roleTasks)) {
                roleTasks.forEach(roleTaskDO -> {
                    if (!roleTaskMap.containsKey(roleTaskDO.getTaskCode())) {
                        roleTaskMap.put(roleTaskDO.getTaskCode(), Sets.newHashSet());
                    }
                    roleTaskMap.get(roleTaskDO.getTaskCode()).add(roleTaskDO.getRoleCode());
                });
            }
        }
        // 返回数据
        List<ItemRoleDTO> itemRoleDTOList = Lists.newArrayList();
        Map<String, Set<String>> itemRoleMap = Maps.newHashMap();
        taskItems.forEach(taskItemDO -> {
            if (!itemRoleMap.containsKey(taskItemDO.getItemCode())) {
                itemRoleMap.put(taskItemDO.getItemCode(), Sets.newHashSet());
            }
            if (roleTaskMap.containsKey(taskItemDO.getTaskCode())) {
                itemRoleMap.get(taskItemDO.getItemCode()).addAll(roleTaskMap.get(taskItemDO.getTaskCode()));
            }
        });
        items.forEach(itemDO -> {
            ItemRoleDTO itemRoleDTO = BeanCopierUtil.copy(ItemRoleDTO.class, itemDO);
            itemRoleDTO.setRoleCodeList(itemRoleMap.getOrDefault(itemDO.getItemCode(), Sets.newHashSet()));
            itemRoleDTOList.add(itemRoleDTO);
        });
        return CallResult.success(CallResult.CODE_SUCCESS, "权限与角色关联数据", itemRoleDTOList);
    }

    /**
     * 用户数据列表
     *
     * @param queryDTO  查询对象
     * @param pageLimit 分页对象
     * @return
     */
    public CallResult<PageModel<UserDTO>> userList(UserDTO.Query queryDTO, PageLimit pageLimit) {
        UserDO.Query queryDO = BeanCopierUtil.copy(UserDO.Query.class, queryDTO);
        if (pageLimit != null) {
            PageHelper.startPage(pageLimit.getPageNum(), pageLimit.getPageSize());
        }
        List<UserDO> userDOS = userMapper.userList(queryDO);
        if (CollectionUtils.isEmpty(userDOS)) {
            return CallResult.failure("没有用户数据");
        }
        List<UserDTO> userDTOS = BeanCopierUtil.copyList(UserDTO.class, userDOS);
        // 接收返回分页数据
        if (userDOS instanceof Page) {
            Page pageResult = (Page) userDOS;
            return CallResult.success(PageModel.newInstance(followHandleUser(userDTOS),
                    pageResult.getPageNum(),
                    pageResult.getPageSize(), pageResult.getPages(), pageResult.getTotal()));
        } else {
            return CallResult.success(PageModel.newInstance(followHandleUser(userDTOS)));
        }
    }

    /**
     * 查询用户角色并放入用户对象中
     *
     * @param userDTOS
     * @return
     */
    public List<UserDTO> followHandleUser(List<UserDTO> userDTOS) {
        List<Integer> userIds = userDTOS.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        // 查询用户角色
        List<UserRoleDO> userRoles = userRoleMapper.usersRoleList(userIds);
        if (CollectionUtils.isEmpty(userRoles)) {
            return userDTOS;
        }
        Map<Integer, List<String>> userRoleMap = Maps.newHashMap();
        userRoles.forEach(userRoleDO -> {
            userRoleMap.computeIfAbsent(userRoleDO.getUserId(), k -> Lists.newArrayList()).add(userRoleDO.getRoleCode());
        });

        return userDTOS;
    }

    /**
     * 编辑用户信息
     *
     * @param userUpdateDTO
     * @return
     */
    public CallResult<String> editUser(UserDTO userUpdateDTO) {
        UserDO userDO = BeanCopierUtil.copy(UserDO.class, userUpdateDTO);
        // 更新用户数据
        if (userMapper.editUser(userDO) == 0) {
            return CallResult.failure("编辑用户信息失败");
        }
        // 判断更新用户角色关系数据
        if (!Objects.isNull(userUpdateDTO.getRoleCodes())
                && !editUserRoles(userUpdateDTO.getUserId(), userUpdateDTO.getRoleCodes()).isSuccess()) {
            return CallResult.failure("编辑用户角色数据失败");
        }
        return CallResult.success();
    }

    /**
     * 更新用户角色数据
     *
     * @param userId
     * @param roleCodes
     * @return
     */
    public CallResult<String> editUserRoles(Integer userId, List<String> roleCodes) {
        // 清除用户角色数据
        userRoleMapper.cleanUserRole(userId);
        // 保存新用户角色数据
        CallResult<String> saveResult = saveUserRole(userId, roleCodes);
        if (!saveResult.isSuccess()) {
            return CallResult.failure("更新用户角色失败");
        }
        return CallResult.success();
    }

    /**
     * 用户map数据
     *
     * @param userIds
     * @return
     */
    public Map<Integer, UserDTO> userDTOMap(List<Integer> userIds) {
        Map<Integer, UserDTO> userDTOMap = Maps.newHashMap();
        List<UserDTO> users = userList(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return userDTOMap;
        }
        users.forEach(userDto -> {
            userDTOMap.put(userDto.getUserId(), userDto);
        });
        return userDTOMap;
    }

    /**
     * 按用户ids查询用户列表数据
     * @param userIds
     * @return
     */
    public List<UserDTO> userList(List<Integer> userIds) {
        UserDO.Query query = new UserDO.Query();
        query.setUserIds(userIds);
        List<UserDO> users = userMapper.userList(query);
        return BeanCopierUtil.copyList(UserDTO.class, users);
    }


    /**
     * 用户id name map集合
     * @param userIds
     * @return
     */
    public Map<Integer, String> userNameMap(List<Integer> userIds) {
        Map<Integer, String> userDTOMap = Maps.newHashMap();
        List<UserDTO> users = userList(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return userDTOMap;
        }
        users.forEach(userDto -> {
            userDTOMap.put(userDto.getUserId(), userDto.getNickName());
        });
        return userDTOMap;
    }

    /**
     * 查询用户权限项
     * @param userDto
     */
    public void queryUserTasks(UserDTO userDto) {
        RoleTaskDO.Query roleTaskQuery = new RoleTaskDO.Query();
        roleTaskQuery.setRoleCodes(userDto.getRoleCodes());
        userDto.setTaskCodes(roleTaskMapper.roleTaskList(roleTaskQuery).stream().map(
                RoleTaskDO::getTaskCode
        ).collect(Collectors.toList()));
    }

    /**
     * 根据角色查询用户列表
     * @param roleCode
     * @return
     */
    public CallResult<List<UserDTO>> getUserListByRoleCode(String roleCode) {
        UserDO.Query query = new UserDO.Query();
        query.setRoleCodes(Lists.newArrayList(roleCode));
        List<UserDO> userList = userMapper.userList(query);
        if(CollectionUtils.isEmpty(userList)){
            return CallResult.failure("无对应角色用户");
        }
        return CallResult.success(BeanCopierUtil.copyList(UserDTO.class,userList));
    }
}
