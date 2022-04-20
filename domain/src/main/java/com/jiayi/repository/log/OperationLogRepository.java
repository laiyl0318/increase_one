package com.jiayi.repository.log;

import com.jiayi.dao.data.log.OperationLogDO;
import com.jiayi.dao.mapper.log.OperationLogMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 操作日志
 *
 * @author wyq
 * @date 2020-10-14
 */
@Component
public class OperationLogRepository {
    @Resource
    private OperationLogMapper operationLogMapper;

    public OperationLogDO findOne(Long logId) {
        return operationLogMapper.selectByPrimaryKey(logId);
    }

    public int insert(OperationLogDO operationLogDO) {
        return operationLogMapper.insertSelective(operationLogDO);
    }
}
