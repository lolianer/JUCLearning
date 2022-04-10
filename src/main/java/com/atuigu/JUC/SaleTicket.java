package com.atuigu.JUC;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用synchronized和lock的方式分别控制卖票的票数
 *
 * @author shen_wzhong
 * @create 2022-04-10 17:11
 */

class Ticket {
    private int number = 30;
    private Lock lock = new ReentrantLock();

    public synchronized void saleTicket1() {
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + "  卖出第  " + number);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            number--;
        }
    }

    public void saleTicket2() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "  卖出第  " + number);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                number--;
            }
            // access the resource protected by this lock
        } finally {
            lock.unlock();
        }
    }
}

public class SaleTicket {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 40; i++) {
                    ticket.saleTicket1();
                }
            }
        };

        Thread thread1 = new Thread(run, "AA");
        Thread thread2 = new Thread(run, "BB");
        Thread thread3 = new Thread(run, "CC");


        thread1.start();
        thread2.start();
        thread3.start();

    }
}
