package com.jyd.juc.ch1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j
public class ThreadNewTest3 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask<Integer> f = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running...");
                Thread.sleep(1000);
                return 10;
            }
        });

        Thread t = new Thread(f,"t1");
        t.start();
        log.debug("{}",f.get());
    }
}
