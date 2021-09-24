package com.whw.footstones.core.util;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Date 2020/7/2 13:41
 * @Author cly
 **/
public class MyLock {
    private final ConcurrentMap<Thread, LockData> THREAD_DATA;
    private final RedisCommands<String, String> redisCommands;
    private final String lockKey;
    private final String lockValue;

    public MyLock(RedisCommands<String, String> redisCommands, String lockKey, String lockValue) {
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.redisCommands = redisCommands;
        this.THREAD_DATA = new ConcurrentHashMap<>();
    }

    public boolean getLock(int lockTime) {
        //判断当前线程是否已经持有锁
        Thread currentThread = Thread.currentThread();
        LockData lockData = THREAD_DATA.get(currentThread);
        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            return true;
        } else {
            if ("OK".equals(redisCommands.set(lockKey, lockValue, SetArgs.Builder.nx().ex(lockTime)))) {
                LockMan lockMan = new LockMan(lockKey, lockValue, lockTime, redisCommands);
                Thread daemonThread = new Thread(lockMan);
                daemonThread.setDaemon(true);
                daemonThread.start();
                LockData newLockData = new LockData(currentThread, lockKey, lockValue, daemonThread, lockMan);
                THREAD_DATA.put(currentThread, newLockData);
                return true;
            } else {
                return false;
            }
        }
    }

    public void release() {
        Thread currentThread = Thread.currentThread();
        LockData lockData = THREAD_DATA.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock");
        } else {
            int lockCount = lockData.lockCount.decrementAndGet();
            if (lockCount <= 0) {
                if (lockCount < 0) {
                    throw new IllegalMonitorStateException("Lock count has gone negative for lock");
                } else {
                    //当 lockCount 等于0时，代表所有重入情况释放完毕，此时可以结束续费线程，并释放锁
                    try {
                        //停止续费线程
                        lockData.lockMan.stop();
                        lockData.daemonThread.interrupt();
                        //释放锁
                        redisCommands.eval("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                                ScriptOutputType.INTEGER,
                                new String[]{lockKey}, lockValue);
                        System.out.println("---locked release---");
                    } finally {
                        THREAD_DATA.remove(currentThread);
                    }
                }
            } else {
                System.out.println("---relocked release---");
            }
        }
    }

    private static class LockData {
        final Thread owningThread;
        final String lockKey;
        final String lockValue;
        final Thread daemonThread;
        final LockMan lockMan;
        final AtomicInteger lockCount;

        private LockData(Thread owningThread, String lockKey, String lockValue, Thread daemonThread, LockMan lockMan) {
            this.owningThread = owningThread;
            this.lockKey = lockKey;
            this.lockValue = lockValue;
            this.daemonThread = daemonThread;
            this.lockMan = lockMan;
            this.lockCount = new AtomicInteger(1);
        }
    }
}
