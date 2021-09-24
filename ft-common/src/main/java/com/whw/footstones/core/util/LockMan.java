package com.whw.footstones.core.util;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @Description
 * @Date 2020/7/2 13:40
 * @Author cly
 **/
public class LockMan implements Runnable {
    private final String key;
    private final String value;
    private int lockTime;
    private RedisCommands commands;
    private volatile boolean isOk = true;

    public LockMan(String key, String value, int lockTime, RedisCommands commands) {
        this.key = key;
        this.value = value;
        this.lockTime = lockTime;
        this.commands = commands;
    }

    @Override
    public void run() {
        while (isOk) {
            try {
                //让线程休息会，别老占用资源
                Thread.sleep(lockTime * 1000 / 2);
                //KEYS 参数
                String[] keys = new String[]{key};
                //ARGV 参数
                Object[] values = new Object[]{value, String.valueOf(lockTime)};
                //这里采用 lua 脚本 “续费锁” ，注意要续费是自己持有的锁， value 值唯一确认现在这把锁是自己持有的
                Object eval = commands.eval("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('expire', KEYS[1],ARGV[2]) else return 0 end", ScriptOutputType.INTEGER, keys, values);
                if (Integer.parseInt(eval.toString()) == 1) {
                    System.out.println("续费成功，将锁超时时间重置为 " + lockTime + "s");
                } else {
                    stop();
                    System.out.println("续费失败");
                }

            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                System.out.println("续费线程异常");
                e.printStackTrace();
            }
        }
        System.out.println("续费结束");
    }

    public void stop() {
        this.isOk = false;
    }
}
