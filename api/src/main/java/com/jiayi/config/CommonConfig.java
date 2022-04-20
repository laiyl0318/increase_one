package com.jiayi.config;

import com.jiayi.common.util.SnowFlakeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author laiyilong
 */
@Configuration
public class CommonConfig {
    @Bean
    public SnowFlakeUtil snowFlakeUtil() {
        return new SnowFlakeUtil(0, 0);
    }
}
