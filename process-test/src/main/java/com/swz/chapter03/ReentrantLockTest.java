package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**可重入锁 ReentrantLock的测试
 * @author shen_wzhong
 * @create 2022-04-13 16:59
 */
@Slf4j(topic = "c.ReentrantLockTest")
public class ReentrantLockTest {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            try {
                if (! lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.debug("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {//也可以让别的线程打断等待，就进入了异常
                e.printStackTrace();
                log.debug("获取不到锁");
                return;
            }
            try {
                log.debug("获取到了锁");
            } finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.debug("获取到了锁");
        t1.start();
        Sleeper.sleep(1);
        log.debug("释放了锁");
        lock.unlock();
    }


    /**
     * 两个 synchronized 竞争锁，在等待锁时，别的线程打断不了
     * @param args
     */
    public static void main2(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            synchronized (lock) {
                log.debug("获取到锁");
            }
            log.debug("没有获得锁，返回");
        },"t1");

        synchronized (lock) {
            log.debug("获取到锁");
            t1.start();
            Sleeper.sleep(1);
            log.debug("打断 t1");
            t1.interrupt();
            Sleeper.sleep(10000);
        }
    }



    public static void main1(String[] args) {

        Thread t1 = new Thread(() -> {
            try {
                //如果没有竞争，那么这个方法就会获取lock对象锁
                //如果有竞争，就进入阻塞队列，但是可以由其他线程用 interrupt 中断等待，进入异常
                log.debug("尝试获得锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁，返回");
                return;
            }

            try {
                log.debug("获取到锁");
            } finally {
                lock.unlock();
            }
        },"t1");
        lock.lock();
        t1.start();

        Sleeper.sleep(1);
        log.debug("打断 t1");
        t1.interrupt();
    }

    /**
     * 以下方法都是测试可重入
     */
    public static void method1() {
        lock.lock();
        try {
            log.debug("execute method1");
            method2();
        } finally {
            lock.unlock();
        }
    }

    public static void method2() {
        lock.lock();
        try {
            log.debug("execute method2");
            method3();
        } finally {
            lock.unlock();
        }
    }

    public static void method3() {
        lock.lock();
        try {
            log.debug("execute method3");
        } finally {
            lock.unlock();
        }
    }
}
