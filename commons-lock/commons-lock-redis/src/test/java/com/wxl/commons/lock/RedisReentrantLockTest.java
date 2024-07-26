package com.wxl.commons.lock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;

import java.time.Duration;

/**
 * Created by wuxingle on 2018/03/12
 */
public class RedisReentrantLockTest {

    private static Pool<Jedis> pool;

    @BeforeAll
    public static void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(5);
        poolConfig.setMaxWait(Duration.ofSeconds(5));
        poolConfig.setTestOnBorrow(true);

        pool = new JedisPool(poolConfig, "localhost", 6379, 5000);

    }

    @AfterAll
    public static void after() {
        pool.destroy();
    }

    @Test
    public void test() throws Exception {
        RedisReentrantLock lock = new RedisReentrantLock(
                pool, "testLock", 10000);
        lock.lock();

        Thread.sleep(5000);

        lock.unlock();
    }


    @Test
    public void testLock() throws Exception {
        RedisReentrantLock[] locks = new RedisReentrantLock[10];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new RedisReentrantLock(pool, "testLock", 10000);
        }
        ConcurrentTestHelper.testConcurrent(locks, 100);
    }


}