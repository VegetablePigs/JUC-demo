package com.jyd.juc.ch06;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class TestAtomicInteger {

    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(5);
//        System.out.println(i.incrementAndGet()); //1 ++i 自增1 1
//        System.out.println(i.getAndIncrement()); //1 i++ 1 2
////        System.out.println(i.getAndDecrement()); // i--
//
//        System.out.println(i.getAndAdd(5)); //2 先获取2 再加 5 7
//        System.out.println(i.addAndGet(5)); //12 先加再获取 5+7 12
                                        //读取到    设置值
//        i.updateAndGet(value -> value * 10); // 乘法 做复杂运算 结果set回 i
//        System.out.println(i.getAndUpdate(v -> v / 2));

        // 模拟 updateAndGet 原理
        updateAndGet(i,p -> p / 2);

        System.out.println(i.get());

    }
    public static int updateAndGet(AtomicInteger i, IntUnaryOperator operator){
        while (true){
            int prev =  i.get();
            int next = operator.applyAsInt(prev);
            if (i.compareAndSet(prev,next)){
                return next;
            }
        }
    }

}
