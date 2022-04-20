package com.jiayi.model.annotations;

/**
 * 使用redis锁的方法，第一个参数要实现此接口
 * @author cjw
 */
public interface RedisLockInterface {
    /**
     * redis锁使用的数据key
     * @return
     */
    String redisLockKey();
}
