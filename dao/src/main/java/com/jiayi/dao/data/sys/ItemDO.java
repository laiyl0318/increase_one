package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDO {
    private Integer itemId;

    private String itemCode;

    private String itemName;

    private String controller;

    private String methodName;

    private String description;

    private Byte itemType;

    private Byte allowed;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class Query extends ItemDO {
        private List<Byte> allowedList;
    }
}