package com.jiayi.model.response.sys;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author laiyilong
 */
@Data
public class MenuResponse implements Serializable {
    private static final long serialVersionUID = -2538884893563183496L;

    private String menuCode;

    private String menuName;

    private String router;

    private String icon;

    private Byte isIframe;

    private Byte isTarget;

    private String url;

    private String remark;

    private List<MenuResponse> children;
}
