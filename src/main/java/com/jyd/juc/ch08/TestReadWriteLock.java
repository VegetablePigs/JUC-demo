package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

@Slf4j
public class TestReadWriteLock {
    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();

        new Thread(()->{
            dataContainer.read();
        },"t1").start();

        sleep(1000);
        new Thread(()->{
            dataContainer.write();
        },"t2").start();
    }
}

@Slf4j
class DataContainer{
    private Object data;

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();


    public Object read(){
        log.debug("获取读锁");
        r.lock();
        try {
            log.debug("读取");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return data;
        } finally {
            log.debug("释放读锁");
            r.unlock();
        }
    }

    public void write(){
        log.debug("获取写锁");
        w.lock();
        try {
            log.debug("写入");
        }finally {
            log.debug("释放写锁");
            w.unlock();
        }
    }
}
