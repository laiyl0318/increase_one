package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.ItemDO;

import java.util.List;

public interface ItemMapper {

    /**
     * 保存权限项
     *
     * @param record
     * @return
     */
    int insertSelective(ItemDO record);

    /**
     * 查询单个权限项
     *
     * @param itemId
     * @return
     */
    ItemDO selectByPrimaryKey(Integer itemId);

    /**
     * 权限项列表
     *
     * @param query
     * @return
     */
    List<ItemDO> itemList(ItemDO.Query query);

    /**
     * 批量保存权限项
     *
     * @param items
     * @return
     */
    int batchAdd(List<ItemDO> items);

    /**
     * 编辑权限项
     *
     * @param item
     * @return
     */
    int editItem(ItemDO item);
}