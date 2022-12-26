package com.jyd.juc.ch1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadNewTest {

    public static void main(String[] args) {
        Thread t = new Thread(){
            @Override
            public void run() {
                log.debug("hello");
            }
        };
        t.setName("t1");
        t.start();
        log.debug("hello");
    }

}
