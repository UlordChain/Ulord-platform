package one.ulord.upaas.ucwallet.service.base.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Monitor the thread pool and printing logs
 *
 * @author chenxin
 * @since 2018-08-15
 */
public class VisiableThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(VisiableThreadPoolTaskExecutor.class);

    private void showThreadPoolInfo(String prefix){
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        if(null==threadPoolExecutor){
            return;
        }

        // active：Number of threads being processed
        // wait：The number of threads waiting to be processed
        logger.info("{}, active[{}], wait[{}]",
                this.getThreadNamePrefix(),
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getQueue().size());

        // total：Total number of threads submitted
        // completed：Number of threads completed
//        logger.info("{}, {},total[{}], completed[{}], active[{}], wait[{}]",
//                this.getThreadNamePrefix(),
//                prefix,
//                threadPoolExecutor.getTaskCount(),
//                threadPoolExecutor.getCompletedTaskCount(),
//                threadPoolExecutor.getActiveCount(),
//                threadPoolExecutor.getQueue().size());
    }

    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo("do execute");
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        showThreadPoolInfo("do execute");
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo("do submit");
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo("do submit");
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo("do submitListenable");
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        showThreadPoolInfo("do submitListenable");
        return super.submitListenable(task);
    }
}