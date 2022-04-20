package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单对象
 *
 * @author laiyilong
 */
@Data
public class MenuDTO implements Serializable {
    private static final long serialVersionUID = -8929356404943544120L;

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
}
