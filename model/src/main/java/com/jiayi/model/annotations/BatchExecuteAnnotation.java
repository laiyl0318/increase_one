package com.jiayi.model.annotations;

import com.jiayi.common.util.constants.CommonConstants;
import com.jiayi.model.enums.BatchExecuteTypeEnum;

import java.lang.annotation.*;

/**
 * 批量执行、批量查询注解
 *
 * @author cjw
 * @date 2020-10-15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BatchExecuteAnnotation {
    /**
     * execute 不接受返回值，
     * query 接受返回值
     *
     * @return
     */
    BatchExecuteTypeEnum value() default BatchExecuteTypeEnum.EXECUTE;

    /**
     * 分批处理数量
     *
     * @return
     */
    int batchNum() default CommonConstants.IMPORT_DATA_BATCH;
}

