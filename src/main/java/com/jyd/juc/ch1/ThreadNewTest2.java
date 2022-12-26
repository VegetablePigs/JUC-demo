package com.jyd.juc.ch1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadNewTest2 {

    public static void main(String[] args) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                log.debug("running");
            }
        };

        Thread t = new Thread(r,"t1");
        t.start();
    }
}
