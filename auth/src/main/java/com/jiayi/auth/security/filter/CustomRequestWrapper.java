package com.jiayi.auth.security.filter;

import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by cjw on 2017/9/29.
 */
public class CustomRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, String[]> params = Maps.newHashMap();

    /**
     * 请求消息体
     */
    private String inputContent;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public CustomRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            BufferedReader br = request.getReader();
            inputContent = "";
            String bufferStr;
            while ((bufferStr = br.readLine()) != null) {
                inputContent += bufferStr;
            }
        } catch (Exception ex) {

        }
        params.putAll(request.getParameterMap());
    }

    /**
     * 重载构造方法
     */
    public CustomRequestWrapper(HttpServletRequest request, Map<String, Object> extendParams) {
        this(request);
        try {
            BufferedReader br = request.getReader();
            inputContent = "";
            String bufferStr;
            while ((bufferStr = br.readLine()) != null) {
                inputContent += bufferStr;
            }
        } catch (Exception ex) {

        }
        //这里将扩展参数写入参数表
        addAllParameters(extendParams);
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values;
    }

    public String getInputContent() {
        return inputContent;
    }

    public void addAllParameters(Map<String, Object> otherParams) {
        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                params.put(name, (String[]) value);
            } else if (value instanceof String) {
                params.put(name, new String[]{(String) value});
            } else {
                params.put(name, new String[]{String.valueOf(value)});
            }
        }
    }
}
