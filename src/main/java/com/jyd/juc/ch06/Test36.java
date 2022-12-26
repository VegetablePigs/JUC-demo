package com.jyd.juc.ch06;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static java.lang.Thread.sleep;

@Slf4j
public class Test36 {
    // ABA 问题  其他线程修改了值 CAS感知不到
//    static AtomicReference<String> ref = new AtomicReference<>("A");
    // 解决 加上版本号
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);
    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值 A
        // 这个共享变量被它线程修改过？
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("版本号：{}",stamp);
        other();

        sleep(1000);
        // 尝试改为 C
        log.debug("版本号：{}",stamp);
        log.debug("change A->C {}", ref.compareAndSet(prev, "C",stamp,stamp + 1));
    }

    private static void other() throws InterruptedException {

        new Thread(() -> {
            int stamp = ref.getStamp();
            log.debug("版本号：{}",stamp);
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B",stamp,stamp +1));
        }, "t1").start();

        sleep(500);

        new Thread(() -> {
            int stamp = ref.getStamp();
            log.debug("版本号：{}",stamp);
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A",stamp,stamp +1));
        }, "t2").start();

    }
}
