package com.jiayi.dao.data.log;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author wyq
 * @date 2020-10-14
 */
@Data
public class OperationLogDO {
    private Long logId;

    private Long objectId;

    private Byte objectType;

    private Byte operateType;

    private String remark;

    private String operateOldData;

    private String operateNewData;

    private Integer operatorId;

    private LocalDateTime operateTime;
}