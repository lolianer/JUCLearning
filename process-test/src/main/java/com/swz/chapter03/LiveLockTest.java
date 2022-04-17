package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shen_wzhong
 * @create 2022-04-13 16:31
 */
@Slf4j(topic = "c.LiveLockTest")
public class LiveLockTest {
    static volatile int count = 10;
    static final Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                Sleeper.sleep(0.2);
                count--;
                log.debug("count: {}", count);
            }
        }, "t1").start();

        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                Sleeper.sleep(0.2);
                count++;
                log.debug("count: {}", count);
            }
        }, "t2").start();
    }
}
