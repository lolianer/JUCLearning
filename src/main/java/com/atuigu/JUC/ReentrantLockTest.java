package com.atuigu.JUC;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**测试ReentrantLock可重入锁
 * 可重入锁：一个线程在执行一个带锁的方法，该方法中又调用了另一个需要相同锁的方法，则该线程可以直接执行调用的方法【即可重入】，而无需重新获得锁
 * @author shen_wzhong
 * @create 2022-04-10 18:25
 */
public class ReentrantLockTest {
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public static void main(String[] args) {
        final ReentrantLockTest test = new ReentrantLockTest();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                test.insert(Thread.currentThread());
            }
        };

        new Thread(runnable,"A") {}.start();
        new Thread(runnable,"B") {}.start();

    }

    public void insert(Thread thread) {
        Lock lock = new ReentrantLock(); //注意这个地方
        lock.lock();
        try {
            System.out.println(thread.getName() + "得到了锁");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {

        } finally {
            System.out.println(thread.getName() + "释放了锁");
            lock.unlock();
        }
    }
}
