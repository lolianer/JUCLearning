package com.swz.chapter04;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**wait/notify/notifyAll方法的测试
 * @author shen_wzhong
 * @create 2022-04-12 18:11
 */
@Slf4j(topic = "c.NotifyTest")
public class NotifyTest {
    final static Object obj = new Object();
    public static void main(String[] args) {

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        },"t2").start();
        // 主线程两秒后执行
        Sleeper.sleep(2);
        log.debug("唤醒 obj 上其它线程");

        synchronized (obj) {
//            obj.notify(); // 唤醒obj上一个线程
             obj.notifyAll(); // 唤醒obj上所有等待线程
        }
    }
}
