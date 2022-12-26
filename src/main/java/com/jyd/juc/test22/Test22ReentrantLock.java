package com.jyd.juc.test22;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j
public class Test22ReentrantLock {
    private static ReentrantLock lock = new ReentrantLock();

    // 可重入
    /*public static void main(String[] args) {
        lock.lock();
        try {
            log.info(" entry main");
            m1();
        }finally {
            lock.unlock();
        }
    }

    public static void m1(){
        lock.lock();
        try {
            log.info(" entry m1");
            m2();
        }finally {
            lock.unlock();
        }
    }

    public static void m2(){
        lock.lock();
        try {
            log.info(" entry m2");
        }finally {
            lock.unlock();
        }
    }*/

    // 可打断
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            try {
                //如果没有竞争那么此方法就会获取Lock对象锁
                // 如果有竞争就进入阻塞队列，可以被其它线程用interruput方法打断
                log.info("尝试获取锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                log.info("被打断");
                e.printStackTrace();
                return;
            }
            try {
                log.info("获取到锁");
            }finally{
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();

        sleep(1000);

        log.info("打断 t1");
        t1.interrupt();


    }
}
