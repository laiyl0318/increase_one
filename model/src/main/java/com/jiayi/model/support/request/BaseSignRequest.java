package com.jiayi.model.support.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author cjw
 * @date 2020-10-29
 */
@Data
public class BaseSignRequest implements Serializable {
    private static final long serialVersionUID = 8194028156970536774L;

    /**
     * 对外应用id
     */
    @Size(groups = CheckSignProfile.class, max = 32, message = "应用id长度过长")
    @NotBlank(groups = CheckSignProfile.class, message = "应用id不可为空")
    private String appId;

    /**
     * 外部应用签名
     */
    @NotBlank(groups = CheckSignProfile.class, message = "签名不可为空")
    private String sign;

    /**
     * 请求时间戳：13位
     */
    @Size(groups = CheckSignProfile.class, min = 13, max = 13, message = "请求时间戳必须为13位")
    @NotBlank(groups = CheckSignProfile.class, message = "请求时间戳不可为空")
    private String timestamp;

    /**
     * 请求消息体 jsonString
     */
    private String bizContent;

    /**
     * 验证场景：验证签名
     */
    public interface CheckSignProfile {

    }
}
