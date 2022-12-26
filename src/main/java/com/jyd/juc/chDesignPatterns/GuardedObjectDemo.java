package com.jyd.juc.chDesignPatterns;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jyd.juc.chDesignPatterns.MailBoxes.createGuardedObject;
import static java.lang.Thread.sleep;

/**
 * 同步模式 保护性暂停
 */
@Slf4j
public class GuardedObjectDemo {

    // 线程1 等待 线程2的下载结果
    public static void main(String[] args) throws InterruptedException {
//        GuardedObject guardedObject = new GuardedObject();
//        new Thread(()->{
//            log.debug("等待结果");
//            List<String> res = (List<String>) guardedObject.get(2000);
//            log.debug("结果：{}",res);
//        },"t1").start();
//
//        new Thread(()->{
//            log.debug("执行下载");
//            try {
////                List<String> list = Downloader.download();
//                sleep(1000);
//                guardedObject.complete(null);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        },"t2").start();
//    }
        for (int i = 0; i < 3; i++) {
            new People().start();
        }

        sleep(1000);

        for (Integer id : MailBoxes.getIds()) {
            new PostMan(id,"内容："+ id).start();
        }

    }
}

    @Slf4j
    class People extends Thread {
        @Override
        public void run() {
            // 收信
            GuardedObject guardedObject = createGuardedObject();
            log.debug("开始收信 id:{}", guardedObject.getId());
            Object mail = guardedObject.get(5000);
            log.debug("收到信 id:{}，内容：{}", guardedObject.getId(), mail);
        }
    }

    @Slf4j
    class PostMan extends Thread {
        private int id;
        private String mail;

        public PostMan(int id, String mail) {
            this.id = id;
            this.mail = mail;
        }

        @Override
        public void run() {
            GuardedObject guardedObject = MailBoxes.getGuardedObject(id);
            log.debug("送信 id:{}，内容：{}", id, mail);
            guardedObject.complete(mail);
        }
    }

    class MailBoxes {

        private static Map<Integer, GuardedObject> boxes = new Hashtable<>();

        private static int id = 1;

        // 产生一个唯一id
        public static synchronized int generateId() {
            return id++;
        }

        public static GuardedObject getGuardedObject(int id){
            return boxes.remove(id);
        }

        public static GuardedObject createGuardedObject() {
            GuardedObject go = new GuardedObject(generateId());
            boxes.put(go.getId(), go);
            return go;
        }

        public static Set<Integer> getIds() {
            return boxes.keySet();
        }
    }

    class GuardedObject {

        // 标识
        private int id;

        public int getId() {
            return id;
        }

        public GuardedObject(int id) {
            this.id = id;
        }

        // 结果
        private Object response;

        // 获取结果
        public Object get(long timeout) {
            synchronized (this) {
                // 没有结果 等待
                // 开始时间
                long begin = System.currentTimeMillis();
                // 经历的时间
                long passedTime = 0;
                while (response == null) {
                    // 这一轮循环应该等待的时间
                    long waitTime = timeout - passedTime;
                    // 经历的时间超过了最大等待时间，退出循环
                    if (waitTime <= 0) {
                        break;
                    }
                    try {
                        this.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 求经历时间
                    passedTime = System.currentTimeMillis() - begin;
                }
                return response;
            }
        }

        // 产生结果
        public void complete(Object response) {
            synchronized (this) {
                // 给结果成员变量赋值
                this.response = response;
                this.notifyAll();
            }
        }
    }

