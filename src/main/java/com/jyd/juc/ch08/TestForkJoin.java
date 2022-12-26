package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class TestForkJoin {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
        System.out.println(pool.invoke(new MyTask(5)));
    }
}

@Slf4j
class MyTask extends RecursiveTask<Integer>{

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        // 终止条件
        if(n==1){
            return 1;
        }
        MyTask t1 = new MyTask(n - 1);
        t1.fork(); // 拆分 让一个线程去执行此任务

        Integer join = t1.join();// 获取任务结果
        int result = n + join;

        return result;
    }
}