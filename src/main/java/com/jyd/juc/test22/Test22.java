package com.jyd.juc.test22;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j
public class Test22 {
    private static ReentrantLock lock = new ReentrantLock();

    // 锁超时
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            log.info("尝试获取锁");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.info("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("获取不到锁");
                return;
            }
            try {
                log.info("获取到锁");
            }finally{
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.info("获取到锁");
        sleep(1000);
        lock.unlock();
        log.info("释放了锁");
        t1.start();
    }
}
