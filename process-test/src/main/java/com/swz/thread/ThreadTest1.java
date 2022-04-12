package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shen_wzhong
 * @create 2022-04-11 9:45
 */
@Slf4j(topic = "c.ThreadTest1")
public class ThreadTest1 {
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.debug("running");
            }
        };

        new Thread(runnable,"t1").start();
        log.debug("running");
    }
}
