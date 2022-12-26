package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestScheduledExecutor {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);


        log.debug("start");
        // 时间每间隔1秒执行
        pool.scheduleAtFixedRate(()->{
            log.debug("running...");
        },1,1,TimeUnit.SECONDS);

        // 一开始，延时 1s，scheduleWithFixedDelay 的间隔是 上一个任务结束 <-> 延时 <-> 下一个任务开始 所以间隔都是 3s
        pool.scheduleWithFixedDelay(()->{
            log.debug("running...");
        },1,1,TimeUnit.SECONDS);
    }

    // 延时执行
    private static void method1(ScheduledExecutorService pool) {
        pool.schedule(()->{
            log.debug("task1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },1, TimeUnit.SECONDS);

        pool.schedule(()->{
            log.debug("task2");
        },1, TimeUnit.SECONDS);
    }
}
