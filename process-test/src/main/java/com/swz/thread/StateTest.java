package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

/**java中的六种状态的测试
 * @author shen_wzhong
 * @create 2022-04-11 16:01
 */
@Slf4j(topic = "c.StateTest")
public class StateTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("running");
        });//未执行，new

        Thread t2 = new Thread(() -> {
            while (true) {
//                log.debug("running");
            }
        });
        t2.start();//死循环，Runnable

        Thread t3 = new Thread(() -> {
            log.debug("running");
        });
        t3.start();//运行完就结束。Teminated

        Thread t4 = new Thread(() -> {
            synchronized (StateTest.class) {
                try {
                    Thread.sleep(10000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        });//阻塞状态，Timed Waiting 有时限的等待
        t4.start();

        Thread t5 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t5.start();//阻塞，会等待t2的结束，Waiting，会一直等待，直到t2结束

        Thread t6 = new Thread(() -> {
            synchronized (StateTest.class) {
                try {
                    Thread.sleep(10000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t6.start();//因为t2先拿到锁，t6就拿不到锁，等待锁，Blocked
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("t1 state {}",t1.getState());//t1 state NEW
        log.debug("t2 state {}",t2.getState());//t2 state RUNNABLE
        log.debug("t3 state {}",t3.getState());//t3 state TERMINATED
        log.debug("t4 state {}",t4.getState());//t4 state TIMED_WAITING
        log.debug("t5 state {}",t5.getState());//t5 state WAITING
        log.debug("t6 state {}",t6.getState());//t6 state BLOCKED



    }
}
