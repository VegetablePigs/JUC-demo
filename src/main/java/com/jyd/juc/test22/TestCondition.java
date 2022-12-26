package com.jyd.juc.test22;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        // 创建一个新的条件变量 (休息室
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        lock.lock();
        // 进入休息室等待
        condition1.await();

        // 唤醒condition1里某一个线程
        condition1.signal();
        // 全部唤醒
        condition1.signalAll();
    }
}
