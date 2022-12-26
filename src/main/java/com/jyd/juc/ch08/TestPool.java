package com.jyd.juc.ch08;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(
                1, 1500, TimeUnit.MILLISECONDS, 1,
                (queue,task)->{
                    // 拒绝策略
                    // 死等
//                    queue.put(task);
                    // 2）带超时等待
//                    queue.offer(task,500,TimeUnit.MILLISECONDS);
                    // 3)让调用者放弃任务执行
//                    log.debug("放弃{}", task);
                    // 4)让调用者抛出异常
//                    throw new  RuntimeException("任务执行失败 "+ task);
                    // 5)让调用者自己执行任务
                    task.run();
                });
        for (int i = 0; i < 3; i++) {
            int j = i;
            threadPool.execute(()->{
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }
    }
}

/**
 * 拒绝策略
 */
@FunctionalInterface
interface RejectPolicy<T>{
    void reject(BlockingQueue<T> queue,T task);
}

/**
 * 线程池
 */
@Slf4j
class ThreadPool{
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    HashSet<Worker> workers = new HashSet();

    // 核心线程数
    private int coreSize;

    // 获取任务超时时间
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    // 执行任务
    public void execute(Runnable task){
        // 当任务数没有超过 coreSize 时，直接交给worker对象执行
        // 如果任务数超过coreSize 时，加入任务队列暂存
        synchronized (workers){
            if (workers.size() < coreSize){
                Worker worker = new Worker(task);
                log.debug("新增worker：{}，{} ",worker,task);
                workers.add(worker);
                worker.start();
            }else {
//                taskQueue.put(task);
                // 1)死等
                // 2）带超时等待
                // 3)让调用者放弃任务执行
                // 4)让调用者抛出异常
                // 5)让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapacity,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //执行任务
            // 1) 当task 不为空，执行任务
            // 2)当task执行完毕，再接着从任务队列获取任务并执行
//            while (task != null || (task = taskQueue.take()) != null){
            while (task != null || (task = taskQueue.poll(timeout,timeUnit)) != null){
                try {
                    log.debug("正在执行：{} ",task);
                    task.run();
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    task = null;
                }
            }
            // 线程执行完 把线程移除
            synchronized (workers){
                log.debug("worker被移除：{} ",this);
                workers.remove(this);
            }
        }
    }
}

/**
 * 阻塞队列
 * @param <T>
 */
@Slf4j
class BlockingQueue<T>{
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁
    private ReentrantLock lock = new ReentrantLock();

    // 3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5.容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 带超时的等待获取
    public T poll(long timeout, TimeUnit timeUnit){
        lock.lock();
        try{
            // 将超时时间统一转换为纳秒
            long nanos = timeUnit.toNanos(timeout);
            // 是空的就阻塞
            while (queue.isEmpty()){
                try {
                    // 返回的是剩余的时间
                    if (nanos <= 0){
                        return null;
                    }
                   nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列头部获取
            T t = queue.removeFirst();
            // 唤醒fullWaitSet
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take(){
        lock.lock();
        try{
            // 是空的就阻塞
            while (queue.isEmpty()){
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列头部获取
            T t = queue.removeFirst();
            // 唤醒fullWaitSet
            fullWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T task){
        lock.lock();
        try {
            // 队列满了阻塞
            while (queue.size() == capacity ){
                try {
                    log.debug("等待加入任务队列：{} ",task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列：{} ",task);
            queue.addLast(task);
            // 唤醒emptyWaitSet
            emptyWaitSet.signal();
        }finally {
            lock.unlock();
        }
    }

    // 带超时时间的阻塞添加
    public boolean offer(T task,long timeout,TimeUnit timeUnit){
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            // 队列满了阻塞
            while (queue.size() == capacity ){
                try {
                    log.debug("等待加入任务队列：{} ",task);
                    if (nanos <= 0){
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列：{} ",task);
            queue.addLast(task);
            // 唤醒emptyWaitSet
            emptyWaitSet.signal();
            return true;
        }finally {
            lock.unlock();
        }
    }

    // 获取队列大小
    public int size(){
        lock.lock();
        try {
            return queue.size();
        }finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capacity){
                rejectPolicy.reject(this,task);
            }else { // 有空闲
                log.debug("加入任务队列：{} ",task);
                queue.addLast(task);
                // 唤醒emptyWaitSet
                emptyWaitSet.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}