package com.jyd.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

@Slf4j
public class Thear21 {

    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(()->{
                queue.put(new Message(id,"值："+id));
            },"生产者" + i).start();
        }

        new Thread(()->{
            while(true) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = queue.tack();
            }
        },"消费者").start();
    }
}

// 消息队列类
@Slf4j
class MessageQueue{

    // 消息的队列集合
    private LinkedList<Message> list = new LinkedList<>();
    // 消息队列容量
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取消息
    public Message tack(){
        // 检查对列是否为空
        synchronized (list){
            while (list.isEmpty()){
                try {
                    log.info("队列为空了，消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从元素头部获取消息返回
            Message message = list.removeFirst();
            log.info("已消费消息，{}",message);
            list.notifyAll();
            return message;
        }

    }

    // 存入消息
    public void put(Message message){
        synchronized(list){
            // 检查队列是否满了
            while (list.size() == capacity){
                try {
                    log.info("队列已满，生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 将新的消息加到尾部
            list.addLast(message);
            log.info("已生产消息，{}",message);
            list.notifyAll();
        }
    }

}

final class Message{
    private Integer id;
    private Object value;

    public Message(Integer id, Object value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}