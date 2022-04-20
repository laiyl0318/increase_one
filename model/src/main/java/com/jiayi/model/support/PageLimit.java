package com.jiayi.model.support;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页数据传输
 *
 * @author laiyilong
 */
@Data
public class PageLimit implements Serializable {

    private static final long serialVersionUID = -4476205807691195178L;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 单页数量
     */
    private Integer pageSize;

    public PageLimit() {
    }

    public PageLimit(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
