package com.swz.chapter07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**不论是哪个线程池，在线程执行的任务**发生异常后既不会抛出，也不会捕获**，这时就需要我们做一定的处理。
 * @author shen_wzhong
 * @create 2022-04-17 18:57
 */
@Slf4j(topic = "c.ExceptionTest")
public class ExceptionTest {
    public static void main1(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> {
            log.debug("task1");
            int i = 1 / 0;
        });
    }

    public static void main2(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> {
            try {
                log.debug("task1");
                int i = 1 / 0;
            } catch (Exception e) {
                log.error("error:", e);
            }
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<Boolean> f = pool.submit(() -> {
            log.debug("task1");
            int i = 1 / 0;
            return true;
        });
        log.debug("result:{}", f.get());
    }
}
