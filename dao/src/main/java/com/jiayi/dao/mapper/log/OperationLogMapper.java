package com.jiayi.dao.mapper.log;

import com.jiayi.dao.data.log.OperationLogDO;

/**
 * 操作日志
 *
 * @author wyq
 * @date 2020-10-14
 */
public interface OperationLogMapper {
    /**
     * 保存
     *
     * @param record
     * @return
     */
    int insertSelective(OperationLogDO record);

    /**
     * 查询
     *
     * @param logId
     * @return
     */
    OperationLogDO selectByPrimaryKey(Long logId);
}