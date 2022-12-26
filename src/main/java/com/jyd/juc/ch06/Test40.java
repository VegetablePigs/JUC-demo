package com.jyd.juc.ch06;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 字段更新器
 */
public class Test40 {
    public static void main(String[] args) {
        Student stu = new Student();
        // 创建字段更新器
        AtomicReferenceFieldUpdater updater =
                AtomicReferenceFieldUpdater.newUpdater(Student.class,String.class,"name");
        System.out.println(updater.compareAndSet(stu, null, "张三"));
        System.out.println(stu);

    }
}

class Student{
    volatile String name;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}