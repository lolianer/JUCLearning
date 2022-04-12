package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author shen_wzhong
 * @create 2022-04-11 10:00
 */
@Slf4j(topic = "c.CallableTest")
public class CallableTest {
    public static void main(String[] args) {
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running..");
                Thread.sleep(1000);
                return 100;
            }
        });

        Thread t = new Thread(task,"t1");
        t.start();

        try {
            log.debug("{}", task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
