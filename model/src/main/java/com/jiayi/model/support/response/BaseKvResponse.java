package com.jiayi.model.support.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cjw
 * @date 2020-10-15
 */
@Data
public class BaseKvResponse implements Serializable {
    private static final long serialVersionUID = 2426891121402442156L;
    private Object value;
    private Object name;
    private Object parentId;
    private String desc;
    private List<BaseKvResponse> child;

    public BaseKvResponse() {
    }

    public BaseKvResponse(Object value, Object name) {
        this.value = value;
        this.name = name;
    }

    public BaseKvResponse(Object value, Object name, List<BaseKvResponse> child) {
        this.value = value;
        this.name = name;
        this.child = child;
    }

    public static BaseKvResponse instanceWithDesc(Object value, Object name, String desc) {
        BaseKvResponse baseKvResponse = new BaseKvResponse(value, name);
        baseKvResponse.setDesc(desc);
        return baseKvResponse;
    }

    public static BaseKvResponse instanceWithDescChild(Object value, Object name, String desc, List<BaseKvResponse> child) {
        BaseKvResponse baseKvResponse = new BaseKvResponse(value, name);
        baseKvResponse.setDesc(desc);
        baseKvResponse.setChild(child);
        return baseKvResponse;
    }
}
