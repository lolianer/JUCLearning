package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**Semaphore 信号量的使用
 * @author shen_wzhong
 * @create 2022-04-19 11:13
 */
@Slf4j(topic = "c.SemaphoreTest")
public class SemaphoreTest {
    public static void main(String[] args) {
        //1. 创建semaphore对象
        Semaphore semaphore = new Semaphore(3);//同一时刻只能有三个线程
        
        //2. 10个线程同时运行
        for (int i = 0; i < 10; i++) {
            
            new Thread(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running");
                    Sleeper.sleep(1);
                    log.debug("end");
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
