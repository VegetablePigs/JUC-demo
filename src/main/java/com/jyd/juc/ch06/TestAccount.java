package com.jyd.juc.ch06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestAccount {
    public static void main(String[] args) {
        Account.demo(new AccountUnsafe(10000));
        Account.demo(new AccountCas(10000));
    }
}

// 无锁实现
class AccountCas implements Account{

    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        /*while (true){
            // 获取余额最新值
            int prev = balance.get();
            // 要修改的余额 局部变量还没同步到主存
            int next = prev - amount;
            // 真正的修改 同步到主存
            if (balance.compareAndSet(prev, next)) {
                // 修改成功 退出循环
                break;
            }
        }*/

        // 简化
        balance.getAndAdd(-1 * amount);
    }
}

// 加锁
class AccountUnsafe implements Account {
    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this){
            return balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized(this){
            balance -= amount;
        }
    }
}

interface Account{
    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();

        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        ts.forEach(Thread::start);

        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();

        System.out.println(account.getBalance()
                + " cost: " + (end-start)/1000_000 + " ms");
    }
}