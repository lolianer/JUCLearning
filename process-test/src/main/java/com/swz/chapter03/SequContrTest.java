package com.swz.chapter03;

import java.util.concurrent.locks.LockSupport;

/**面试常问：控制线程的执行顺序  必须先 2 后 1 打印
 * @author shen_wzhong
 * @create 2022-04-14 9:25
 */
public class SequContrTest {
    static boolean flag = false;

    /**
     * 尝试join进行
     * @param args
     */
    public static void main(String[] args) {
        Thread t2 = new Thread(() -> {
            System.out.println(2);
        },"t2");

        Thread t1 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        },"t1");

        t1.start();
        t2.start();
    }

    /**
     * 使用park，unpark方法
     * @param args
     */
    public static void main2(String[] args) {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            System.out.println(1);
        },"t1");
        Thread t2 = new Thread(() -> {
            LockSupport.unpark(t1);
            System.out.println(2);
        },"t2");
        t1.start();
        t2.start();
    }
    /**
     * 1.使用wait、notify方法
     */
    public static void main1(String[] args) {
        Object o = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (o) {
                while (!flag) {
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(1);
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (o) {
                System.out.println(2);
                flag = true;
                o.notify();
            }
        });

        t1.start();
        t2.start();
    }
}
