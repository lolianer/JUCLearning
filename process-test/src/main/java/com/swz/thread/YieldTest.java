package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

/**测试setPriority方法和yield
 * 设置优先级以后，线程有可能会多抢占cpu
 * yield方法会让线程放开cpu资源，重新去抢占
 * @author shen_wzhong
 * @create 2022-04-11 11:34
 */
@Slf4j(topic = "c.TestYield")
public class YieldTest {
    public static void main(String[] args) {
        Runnable task1 = () -> {
            int count = 0;
            for (;;) {
                System.out.println("---->1 " + count++);
            }
        };
        Runnable task2 = () -> {
            int count = 0;
            for (;;) {
                Thread.yield();
                System.out.println("              ---->2 " + count++);
            }
        };
        Thread t1 = new Thread(task1, "t1");
        Thread t2 = new Thread(task2, "t2");
//        t1.setPriority(Thread.MIN_PRIORITY);
//        t2.setPriority(Thread.MAX_PRIORITY);
        t1.start();
        t2.start();
    }
}
