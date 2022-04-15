package com.swz.chapter06;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author shen_wzhong
 * @create 2022-04-15 17:29
 */
public class AtomicReferenceFieldUpdaterTest {
    public static void main(String[] args) {
        Student stu = new Student();
        AtomicReferenceFieldUpdater updater =
                AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

        System.out.println(updater.compareAndSet(stu, null, "张三"));
        System.out.println(stu);
    }

    public static void main1(String[] args) {
        Student stu = new Student();
        AtomicReference<Student> ref = new AtomicReference<>(stu);
        Student student = ref.get();
        student.setName("李四");
        boolean b = ref.compareAndSet(student, student);
        System.out.println(b);
    }
}

class Student {
    volatile String name = "张三";

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}
