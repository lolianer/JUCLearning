package com.atuigu.JUC;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**分别测试 synchronized 和 ReentrantReadWriteLock
 * 读写锁的读锁是不互斥的
 * @author shen_wzhong
 * @create 2022-04-10 18:35
 */
public class ReentrantReadWriteLockTest {
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        final ReentrantReadWriteLockTest test = new ReentrantReadWriteLockTest();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                test.get2(Thread.currentThread());
            }
        };

        new Thread(runnable,"A") {}.start();
        new Thread(runnable,"B") {}.start();
    }

    public synchronized void get1(Thread thread) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= 1) {
            System.out.println(thread.getName() + "正在进行读操作");
        }
        System.out.println(thread.getName() + "读操作完毕");
    }
    public void get2(Thread thread) {
        rwl.readLock().lock();
        try {
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() - start <= 1) {
                System.out.println(thread.getName()+"正在进行读操作");
            }
            System.out.println(thread.getName()+"读操作完毕");
        } finally {
            rwl.readLock().unlock();
        }
    }
}
