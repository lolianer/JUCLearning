package com.swz.chapter07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shen_wzhong
 * @create 2022-04-17 15:28
 */
@Slf4j(topic = "c.ThreadPoolExecutorTest")
public class ThreadPoolExecutorTest {

    //newFixedThreadPool 创建固定大小的线程池 的测试
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "myPool_t" + t.getAndIncrement());
            }
        });
        
        pool.execute(() -> {
            log.debug("1");
        });

        pool.execute(() -> {
            log.debug("2");
        });
        
        pool.execute(() -> {
            log.debug("3");
        });
    }
}
