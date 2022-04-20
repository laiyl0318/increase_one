package com.jiayi.common.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象复制工具
 *
 * @author cjw
 * @date 2020-10-31
 */
public class BeanCopierUtil {
    /**
     * 执行复制
     *
     * @param copyToClazz 拷贝出的类
     * @param beCopy      被拷贝对象
     * @return
     */
    public static <T> T copy(Class<T> copyToClazz, Object beCopy) {
        try {
            T copyTo = copyToClazz.newInstance();
            BeanUtils.copyProperties(beCopy, copyTo);
            return copyTo;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 执行复制
     *
     * @param copyToClazz 拷贝出的对象集合
     * @param beCopy      被拷贝对象
     * @return
     */
    public static <T> List<T> copyList(Class<T> copyToClazz, List<?> beCopy) {
        return beCopy.stream().map(beCopyOne -> copy(copyToClazz, beCopyOne)).collect(Collectors.toList());
    }
}
