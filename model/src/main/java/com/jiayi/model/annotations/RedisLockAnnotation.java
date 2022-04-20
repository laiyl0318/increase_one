package com.jiayi.model.annotations;

import com.jiayi.common.components.RedisLock;

import java.lang.annotation.*;


/**
 * 使用此redis注解的方法，
 *          如果有参数：
 *              第一个参数必须实现RedisLockInterface.redisLockKey(),
 *              如果不实现RedisLockInterface,则直接使用第一个参数的toString()值作为redisLockKey
 *          如果没有参数：
 *              使用methodName作为redisLockKey
 * @author wyq
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLockAnnotation {
    /**
     * redis锁前缀，自定义redisKey前缀
     * @return
     */
    String value() default "object";

    /**
     * 锁的过期时间，默认30秒
     * @return
     */
    int timeout() default RedisLock.REDIS_LOCK_EXPIRE_SECOND_30;
}
