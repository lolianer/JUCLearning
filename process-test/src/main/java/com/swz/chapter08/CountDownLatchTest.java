package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**CountDownLatch 测试
 * @author shen_wzhong
 * @create 2022-04-20 8:29
 */
@Slf4j(topic = "c.CountDownLatchTest")
public class CountDownLatchTest {
    public static void main1(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        new Thread(() -> {
            log.debug("begin");
            Sleeper.sleep(1);
            log.debug("完成");
            latch.countDown();
        }).start();
        new Thread(() -> {
            log.debug("begin");
            Sleeper.sleep(2);
            log.debug("完成");
            latch.countDown();
        }).start();
        new Thread(() -> {
            log.debug("begin");
            Sleeper.sleep(1.5);
            log.debug("完成");
            latch.countDown();
        }).start();
        
        
        log.debug("waiting");
        latch.await();
        log.debug("结束等待");
    }

    //和线程池进行配合
    public static void main2(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);
        
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(() -> {
            log.debug("begin");
            Sleeper.sleep(1);
            log.debug("完成");
            latch.countDown();
            log.debug("还需要等待几个线程{}",latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin");
            Sleeper.sleep(2);
            log.debug("完成");
            latch.countDown();
            log.debug("还需要等待几个线程{}",latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin");
            Sleeper.sleep(1.5);
            log.debug("完成");
            latch.countDown();
            log.debug("还需要等待几个线程{}",latch.getCount());
        });
        
        service.submit(() -> {
            try {
                log.debug("waiting");
                latch.await();
                log.debug("结束等待");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        String[] all = new String[10];
        Random random = new Random();

        for (int j = 0; j < 10; j++) {
            int k = j;
            service.submit(() -> {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    all[k] = i + "%";
                    System.out.print("\r" + Arrays.toString(all));//不换行输出，加上一个回车，就可以覆盖原来的输出
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("\n游戏开始");
        service.shutdown();
    }
}
