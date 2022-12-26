package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

@Slf4j
public class TestShutdown {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<String> future1 = pool.submit(() -> {
            log.debug("task1...running....");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task1...finish....");
            return "1";
        });
        Future<String> future2 = pool.submit(() -> {
            log.debug("task2...running....");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task2...finish....");
            return "2";
        });
        Future<String> future3 = pool.submit(() -> {
            log.debug("task3...running....");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task3...finish....");
            return "3";
        });
        /*
        线程池状态变为 SHUTDOWN
        - 不会接收新任务
        - 但已提交任务会执行完
        - 此方法不会阻塞调用线程的执行
        */
        pool.shutdown();
        /*
        线程池状态变为 STOP
        - 不会接收新任务
        - 会将队列中的任务返回
        - 并用 interrupt 的方式中断正在执行的任务
        */
        List<Runnable> runnables = pool.shutdownNow();
    }
}
