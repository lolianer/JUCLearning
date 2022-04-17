package com.swz.chapter07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shen_wzhong
 * @create 2022-04-17 15:54
 */
@Slf4j(topic = "c.NewSingleThreadExecutorTest")
public class NewSingleThreadExecutorTest {
    public static void main(String[] args) {
        test2();
    }
    
    public static void test2() {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            log.debug("1");
            int i = 1 / 0;
        });
        pool.execute(() -> {
            log.debug("2");
        });
        pool.execute(() -> {
            log.debug("3");
        });
    }
}
