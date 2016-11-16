package com.jstudio.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Usage:
 * ExecutorHelper helper = new ExecutorHelper(4);
 * helper.execute(new BackgroundTask(){...});
 * helper.execute(new BackgroundTask(){...});
 * <p/>
 * Created by Jason
 */
@SuppressWarnings("unused")
public class ExecutorHelper {

    private static ExecutorHelper INSTANCE;
    private ExecutorService mThreadPool = null;
    private int mThreadsCount;

    /**
     * 实例化此类并作出初始化操作
     *
     * @param num 线程池中有多少个线程
     */
    private ExecutorHelper(int num) {
        mThreadsCount = num;
        mThreadPool = Executors.newFixedThreadPool(num);
    }

    public static ExecutorHelper getInstance(int poolNum) {
        if (INSTANCE == null) {
            int num = (poolNum > 0 && poolNum < 10) ? poolNum : 4;
            INSTANCE = new ExecutorHelper(num);
        }
        return INSTANCE;
    }

    /**
     * 表示线程池是否关闭，关闭后可重新创建指定个数的线程池
     *
     * @return true表示处于关闭状态
     */
    public static boolean isShutDown() {
        return INSTANCE == null;
    }

    /**
     * 获取线程池中的线程数量
     *
     * @return 线程池中的线程数量
     */
    public int getThreadsCount() {
        return this.mThreadsCount;
    }

    /**
     * 获取ExecutorService实例
     *
     * @return ExecutorService
     */
    public ExecutorService getThreadPool() {
        return this.mThreadPool;
    }

    /**
     * 执行传入的Runnable
     *
     * @param task Runnable实例，可使用匿名内部类
     */
    public void execute(Runnable task) {
        this.getThreadPool().execute(task);
    }


    /**
     * 取消线程池中的任务
     */
    public void shutDown() {
        mThreadPool.shutdown();
        mThreadPool = null;
        INSTANCE = null;
    }
}
