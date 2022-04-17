package com.swz.chapter07;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;

/**
 * @author shen_wzhong
 * @create 2022-04-17 15:45
 */
@Slf4j(topic = "c.SynchronousQueueTest")
public class SynchronousQueueTest {
    public static void main(String[] args) {
        
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();
        
        new Thread(() -> {
            try {
                //创建一个线程，往队列里放任务，但是放不进去，会阻塞
                //直到别的线程从队列中取任务
                log.debug("putting {} ", 1);
                integers.put(1);
                log.debug("{} putted...", 1);
                
                log.debug("putting...{} ", 2);
                integers.put(2);
                log.debug("{} putted...", 2);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();
        
        Sleeper.sleep(1);
        
        new Thread(() -> {
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();
        
        Sleeper.sleep(1);
        
        new Thread(() -> {
            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t3").start();
    }
}
