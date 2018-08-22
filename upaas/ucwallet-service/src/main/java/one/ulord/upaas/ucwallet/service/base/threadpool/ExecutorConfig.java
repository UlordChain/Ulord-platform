package one.ulord.upaas.ucwallet.service.base.threadpool;

import one.ulord.upaas.ucwallet.service.base.contract.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Config springboot thread pool
 *
 * @author chenxin
 * @since 2018-08-15
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);

    @Autowired
    private Provider provider;

    @Bean
    public Executor asyncServiceExecutor() {
        logger.info("======================  start asyncServiceExecutor......");
        //ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 使用VisiableThreadPoolTaskExecutor
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();

        // 配置核心线程数
        executor.setCorePoolSize(Integer.parseInt(provider.getCorePoolSize()));
        // 配置最大线程数
        executor.setMaxPoolSize(Integer.parseInt(provider.getMaxPoolSize()));
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(Integer.parseInt(provider.getKeepAliveSeconds()));
        // 线程池所使用的缓冲队列
        executor.setQueueCapacity(Integer.parseInt(provider.getQueueCapacity()));
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(provider.getThreadNamePrefix());

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
        executor.setAwaitTerminationSeconds(Integer.parseInt(provider.getAwaitTerminationSeconds()));

        // 执行初始化
        executor.initialize();
        return executor;
    }
}
