package com.atguigu.java;

import com.swz.util.Sleeper;
import org.junit.jupiter.api.Test;

/**
 * @author shen_wzhong
 * @create 2022-04-13 9:35
 */
public class Test1 {
    Object response = null;

    public static void main(String[] args) throws InterruptedException {
        Test1 test1 = new Test1();
        Object lock = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("线程进入等待");
                    lock.wait(5000);
                    System.out.println("线程等待结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");
        thread1.start();
//        thread1.join();//这里是让主线程等待，想让thread1以运行完再走
        synchronized (thread1) {
            thread1.wait(1000);
        }
        Sleeper.sleep(2);
        test1.response = new Object();
        System.out.println("main线程已经附上值了");
    }

    public boolean isAlive() {
        if (response == null) {
            return true;
        } else {
            return false;
        }
    }
}
