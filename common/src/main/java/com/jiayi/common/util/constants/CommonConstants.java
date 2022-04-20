package com.jiayi.common.util.constants;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 通用常量定义
 *
 * @author cjw
 */
public class CommonConstants {
    /**
     * 批量导出临界值
     */
    public static final Integer EXPORT_DATA_BATCH = 2000;

    /**
     * 批量导入参考值
     */
    public static final int IMPORT_DATA_BATCH = 2000;
    /**
     * session id md5加密盐值
     */
    public static final String SESSION_SALT = "$1$LAIYILONG";

    /**
     * 设置获取docker容器中的环境变量
     */
    static {
        System.getenv().forEach((key, value) -> {
            System.setProperty(key, value);
        });
    }


    public static class DefineSign {
        /**
         * 未知类型
         */
        public static final String UNKNOWN = "unknown";
        /**
         * 回车换行符
         */
        public static final String LINEFEED = "\n";
        /**
         * 英文逗号
         */
        public static final String COMMA = ",";
        /**
         * 网络URL请求分隔符
         */
        public static final String HTTP_URL_SPLIT = "&";
        /**
         * 网络URL请求等号
         */
        public static final String HTTP_URL_EQUAL = "=";
        /**
         * 英文点
         */
        public static final String DOT = ".";

    }

    /**
     * 线程池
     */
    public static class ThreadPool {
        /**
         * 定时线程池
         */
        private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder()
                .setNameFormat("laiyilong-thread-pool-%d").build();
        public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(200, NAMED_THREAD_FACTORY);

        public static final ExecutorService FIXED_THREAD_POOL = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(1024), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
    }
}
