package com.swz.chapter03;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**面试常问：控制线程的执行顺序
 * 线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现
 * @author shen_wzhong
 * @create 2022-04-14 10:05
 */
public class SequContrTest2 {
    static Thread t1;
    static Thread t2;
    static Thread t3;

    static Object obj = new Object();
    static boolean t1s = false;  //t1线程是否执行完毕
    static boolean t2s = false; //
    static boolean t3s = true; //

    static ReentrantLock lock = new ReentrantLock();
    static Condition t1c = lock.newCondition();
    static Condition t2c = lock.newCondition();
    static Condition t3c = lock.newCondition();

    public static void main(String[] args) {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                try {
                    while (!t3s) {
                        try {
                            t1c.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("a");
                    t1s = true;
                    t3s = false;
                    t2c.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        });

        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                try {
                    while (!t1s) {
                        try {
                            t2c.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("b");
                    t2s = true;
                    t1s = false;
                    t3c.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        });

        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                try {
                    while (!t2s) {
                        try {
                            t3c.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("c");
                    t3s = true;
                    t2s = false;
                    t1c.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * wait/notifyAll 实现
     * @param args
     */
    public static void main2(String[] args) {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (obj) {
                    while (!t3s) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("a");
                    t1s = true;
                    t3s = false;
                    obj.notifyAll();
                }
            }
        },"t1");

        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (obj) {
                    while (!t1s) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("b");
                    t2s = true;
                    t1s = false;
                    obj.notifyAll();
                }
            }
        },"t2");

        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (obj) {
                    while (!t2s) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("c");
                    t3s = true;
                    t2s = false;
                    obj.notifyAll();
                }
            }
        },"t3");

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 使用park、unpark解决
     * @param args
     */
    public static void main1(String[] args) {

        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.print("a");
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        },"t1");

        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print("c");
                LockSupport.unpark(t1);
            }
        },"t3");

        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print("b");
                LockSupport.unpark(t3);
            }
        },"t2");

        t1.start();
        t2.start();
        t3.start();
    }
}
