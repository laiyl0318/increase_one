package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MenuDO {
    private Integer menuId;

    private String menuCode;

    private String menuName;

    private Integer parentId;

    private Integer serial;

    private Byte menuStatus;

    private Byte menuLevel;

    private Byte menuType;

    private String router;

    private String icon;

    private Byte isIframe;

    private Byte isTarget;

    private String url;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class Query extends MenuDO {

    }
}