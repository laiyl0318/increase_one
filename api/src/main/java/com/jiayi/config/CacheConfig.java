package com.jiayi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author laiyilong
 */
@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${redis.host:127.0.0.1}")
    public String host;

    @Value("${redis.port:6379}")
    public Integer port;

    @Value("${redis.lettuce.pool.max-active:8}")
    public Integer lettucePoolMaxActive;

    @Value("${redis.lettuce.pool.max-wait:-1}")
    public Integer lettucePoolMaxWait;

    @Value("${redis.lettuce.pool.max-idle:8}")
    public Integer lettucePoolMaxIdle;

    @Value("${redis.lettuce.pool.min-idle:0}")
    public Integer lettucePoolMinIdle;

    @Value("${server.env-tag:}")
    private String envTag;

    @Resource
    private ObjectMapper redisObjectMapper;

    private RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(host);
        standaloneConfiguration.setPort(port);
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(lettucePoolMaxIdle);
        poolConfig.setMaxTotal(lettucePoolMaxActive);
        poolConfig.setMinIdle(lettucePoolMinIdle);
        poolConfig.setMaxWaitMillis(lettucePoolMaxWait);
        LettuceClientConfiguration clientConfiguration =
                LettucePoolingClientConfiguration.builder().poolConfig(poolConfig).build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateForPermission() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        StringRedisSerializer stringSerializer = new CustomRedisKeyStringSerializer("lyl-permission", envTag);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateForSession() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        StringRedisSerializer stringSerializer = new CustomRedisKeyStringSerializer("lyl-session", envTag);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateForVote() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        StringRedisSerializer stringSerializer = new CustomRedisKeyStringSerializer("lyl-vote", envTag);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    private RedisSerializer<?> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(redisObjectMapper);
        return jackson2JsonRedisSerializer;
    }

    @Bean
    public CacheManager cacheManager() {
        // ????????????????????????????????????30??????
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new CustomRedisKeyStringSerializer("lyl-data", envTag)))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        // ????????????cacheName??????????????????????????????
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<String, RedisCacheConfiguration>(2) {{
            put("wechat_config_base_cache", defaultCacheConfig.entryTtl(Duration.ofDays(15))); //15???
            put("demoCar", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))); // 10??????
            // ...
        }};

        //cacheDefaults ??????????????????????????????????????????  ???????????????????????????????????????????????????
        // withInitialCacheConfigurations ??????cache??????????????????
        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(initialCacheConfiguration)
                .build();
    }

    /**
     * ?????????redis key??????????????????  ????????????hostname??????????????????
     */
    static class CustomRedisKeyStringSerializer extends StringRedisSerializer {
        /**
         * redis ke??????
         */
        private final String keyPrefix;

        /**
         * ??????????????????
         */
        private final String envTag;

        /**
         * redis key????????????
         *
         * @param prefix ??????
         */
        public CustomRedisKeyStringSerializer(String prefix, String envTag) {
            Assert.notNull(prefix, "redis 'prefix' must not be null");
            this.envTag = envTag;
            this.keyPrefix = prefix;
        }

        /**
         * ??????????????????
         *
         * @return
         */
        public String getDockerHostEnv() {
            String env = System.getenv("SYS_ENV");
            return Strings.isNullOrEmpty(env) ? "local" : env;
        }

        @Override
        public byte[] serialize(String string) {
            if (Strings.isNullOrEmpty(envTag)) {
                return super.serialize(keyPrefix + ":" + string);
            } else {
                return super.serialize(envTag + ":" + keyPrefix + ":" + string);
            }
        }

    }

}
