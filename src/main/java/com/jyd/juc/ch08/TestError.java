package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TestError {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        // 不会抛出异常
        pool.submit(()->{
            int i = 1/0;
        });
        // 任务自己手动抛出异常
        pool.submit(()->{
            try {
                int i = 1/0;
            }catch (Exception e) {
                log.error("error:{}",e);
            }
        });

        // 利用返回结果
        Future<Boolean> future = pool.submit(() -> {
            int i = 1 / 0;
            return true;
        });
        log.debug("{}",future.get());

    }
}
