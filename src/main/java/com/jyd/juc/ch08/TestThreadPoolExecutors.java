package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestThreadPoolExecutors {

    public static void main(String[] args) {
        // 固定大小线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"mypool_t"+t.getAndIncrement());
            }
        });
        executorService.execute(()->{
            log.info("1");
        });
        executorService.execute(()->{
            log.info("2");
        });
        executorService.execute(()->{
            log.info("3");
        });

        // 带缓存的线程池
        Executors.newCachedThreadPool();

        // 单一线程池
        Executors.newSingleThreadExecutor();
    }
}
