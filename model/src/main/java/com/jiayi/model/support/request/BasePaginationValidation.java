package com.jiayi.model.support.request;

import com.jiayi.model.support.request.profiles.QueryListProfile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 基本分页信息参数
 *
 * @author wyq
 * @Title: BasePaginationValidation.java
 * @Description: 基本分页信息参数
 */
@Setter
@Getter
@ToString
@Slf4j
public class BasePaginationValidation extends AbstractValidation {
    protected static final String MSG_TITLE = "必要参数: ";
    protected static final String MSG_OTHER_TITLE = "选填参数: ";
    private static final long serialVersionUID = 3884386736943975337L;
    @Min(groups = QueryListProfile.class, value = 1, message = "单页数量不可少于1")
    @Max(groups = QueryListProfile.class, value = 999, message = "单页数量不可大于999")
    private Integer pageSize = 20;

    @Min(groups = QueryListProfile.class, value = 1, message = "页码不可小于1")
    @Max(groups = QueryListProfile.class, value = 999, message = "页码不可大于999")
    private Integer pageNo = 1;
}
