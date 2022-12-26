package com.jyd.juc.testJMM;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

@Slf4j
public class TestJmm1 {
    private volatile static boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while (run) {
//                System.out.println("111");
            }
        },"t1").start();

        sleep(1000);
        log.info("停掉");
        run = false;
    }
}
