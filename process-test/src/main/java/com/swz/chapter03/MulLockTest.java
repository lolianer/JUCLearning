package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试多把锁
 * @author shen_wzhong
 * @create 2022-04-13 15:35
 */
@Slf4j(topic = "c.MulLockTest")
public class MulLockTest {
    public static void main(String[] args) {
        BigRoom bigRoom = new BigRoom();
        new Thread(() -> {
            bigRoom.study();
        },"t1").start();

        new Thread(() -> {
            bigRoom.sleep();
        },"t2").start();
    }
}

@Slf4j(topic = "c.BigRoom")
class BigRoom {
    public void sleep() {
        synchronized (this) {
            log.debug("sleeping 2 小时");
            Sleeper.sleep(2);
        }
    }
    public void study() {
        synchronized (this) {
            log.debug("study 1 小时");
            Sleeper.sleep(1);
        }
    }
}
