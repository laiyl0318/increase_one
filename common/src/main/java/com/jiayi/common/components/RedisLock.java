package com.jiayi.common.components;

import com.jiayi.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * redis 锁操作
 * @author cjw
 */
@Slf4j
@Component
public class RedisLock {

    /**
     * redis锁默认过期时间 30秒
     */
    public static final int REDIS_LOCK_EXPIRE_SECOND_30 = 30;
    @Resource
    private RedisTemplate<String, Object> redisTemplateForVote;
    /**
     * 操作加锁: true 锁定成功，锁定失败
     * @param key
     * @param timeSecond
     * @return
     */
    public boolean lock(String key, Integer timeSecond) {
        try {
            ValueOperations<String, Object> operations = redisTemplateForVote.opsForValue();
            // 如果锁不存在，返回true
            if (Boolean.TRUE.equals(operations.setIfAbsent(key, LocalDateTime.now()))) {
                operations.getOperations().expire(key, timeSecond, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception ex) {
            log.error("业务加锁失败，发生异常", ex);
            throw new BusinessException("业务加锁操作失败");
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public void unlock(String key) {
        try {
            redisTemplateForVote.delete(key);
        } catch (Exception ex) {
            log.error("业务加锁失败，发生异常", ex);
            throw new BusinessException("业务锁移除操作失败");
        }
    }
}
