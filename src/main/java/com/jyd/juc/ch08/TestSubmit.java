package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

@Slf4j
public class TestSubmit {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            // 执行多个任务
            String result = pool.invokeAny(Arrays.asList(
                    () -> {
                        log.debug("begin");
                        sleep(1000);
                        return "1";
                    },
                    () -> {
                        log.debug("begin");
                        sleep(2000);
                        return "2";
                    },
                    () -> {
                        log.debug("begin");
                        sleep(1500);
                        return "3";
                    }
            ));
            log.debug("{}",result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private static void method2(ExecutorService pool) {
        try {
            // 执行多个任务
            List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                    () -> {
                        log.debug("begin");
                        sleep(1000);
                        return "1";
                    },
                    () -> {
                        log.debug("begin");
                        sleep(2000);
                        return "2";
                    },
                    () -> {
                        log.debug("begin");
                        sleep(1500);
                        return "3";
                    }
            ));
            futures.forEach(f->{
                        try {
                            log.debug("{}",f.get());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * submit
     * @param pool
     */
    private static void method1(ExecutorService pool) {
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                sleep(1000);
                log.debug("执行....");
                return "ok";
            }
        });
        try {
            log.debug(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
