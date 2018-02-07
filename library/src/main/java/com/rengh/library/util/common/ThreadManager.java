
package com.rengh.library.util.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rengh on 17-5-29.
 */
public class ThreadManager {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int THREADS_CORE_COUNT = CPU_COUNT + 1;
    private static int sCoreThreadsCount = THREADS_CORE_COUNT;

    private static volatile ThreadManager sInstance;
    private ExecutorService mSingleThreadPool;
    private ExecutorService mCachedThreadPool;
    private ExecutorService mFixedThreadPool;
    private ScheduledExecutorService mScheduledThreadPool;
    private ScheduledExecutorService mScheduledSingleThreadPool;

    private ThreadManager() {
        init();
    }

    private ThreadManager(int coreThreads) throws IllegalArgumentException {
        if (coreThreads <= 0) {
            throw new IllegalArgumentException();
        } else {
            sCoreThreadsCount = coreThreads;
            init();
        }
    }

    private void init() {
        mSingleThreadPool = Executors.newSingleThreadExecutor();
        mCachedThreadPool = Executors.newCachedThreadPool();
        mFixedThreadPool = Executors.newFixedThreadPool(sCoreThreadsCount);
        mScheduledThreadPool = Executors.newScheduledThreadPool(sCoreThreadsCount);
        mScheduledSingleThreadPool = Executors.newSingleThreadScheduledExecutor();
    }

    public static int getCoreThreadsCount() {
        return sCoreThreadsCount;
    }

    public static boolean setCoreTheadsCount(int coreThreads) throws IllegalArgumentException {
        if (null == sInstance) {
            sInstance = new ThreadManager(coreThreads);
            return true;
        }
        return false;
    }

    public static int getMaxThreadsCount() {
        return Integer.MAX_VALUE;
    }

    public static ThreadManager getInstance() {
        if (null == sInstance) {
            sInstance = new ThreadManager();
        }
        return sInstance;
    }

    public ExecutorService getSingleThreadPool() {
        return mSingleThreadPool;
    }

    public ExecutorService getCachedThreadPool() {
        return mCachedThreadPool;
    }

    public ExecutorService getFixedThreadPool() {
        return mFixedThreadPool;
    }

    public ExecutorService getScheduledThreadPool() {
        return mScheduledThreadPool;
    }

    public void excuteSingle(Runnable command) {
        mSingleThreadPool.execute(command);
    }

    public void excuteCached(Runnable command) {
        mCachedThreadPool.execute(command);
    }

    public void excuteFixed(Runnable command) {
        mFixedThreadPool.execute(command);
    }

    public void excuteScheduled(Runnable command, long delay, TimeUnit unit) {
        mScheduledThreadPool.schedule(command, delay, unit);
    }

    public void excuteScheduled(Runnable command, long delay, long reRun, TimeUnit unit) {
        mScheduledThreadPool.scheduleAtFixedRate(command, delay, reRun, unit);
    }

    public void excuteScheduledSingle(Runnable command, long delay, TimeUnit unit) {
        mScheduledSingleThreadPool.schedule(command, delay, unit);
    }

    public void excuteScheduledSingle(Runnable command, long delay, long reRun, TimeUnit unit) {
        mScheduledSingleThreadPool.scheduleAtFixedRate(command, delay, reRun, unit);
    }

}
