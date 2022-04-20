package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author cjw
 * @date 2020-10-28
 */
@Data
public class ItemDTO implements Serializable {

    private static final long serialVersionUID = 3955399432302504661L;

    private Integer itemId;
    private String itemCode;

    private String itemName;

    private Byte itemType;

    private Byte allowed;

    private String controller;

    private String methodName;

    private String description;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
