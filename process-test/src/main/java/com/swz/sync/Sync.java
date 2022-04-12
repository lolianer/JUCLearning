package com.swz.sync;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;

import static java.lang.Thread.sleep;

/**同步等待
 * @author shen_wzhong
 * @create 2022-04-11 9:16
 */
@Slf4j(topic = "c.Sync")
public class Sync {
    public static void main(String[] args) {
        try {
            test1();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static int result = 0;
    private static void test1() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            result = 10;
        }, "t1");
        t1.start();
//        t1.join();
        log.debug("结果为:{}", result);
    }
}
