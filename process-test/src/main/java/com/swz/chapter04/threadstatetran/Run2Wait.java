package com.swz.chapter04.threadstatetran;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**线程的状态的转换
 *      用wait notify 从running 到waiting 转变
 * @author shen_wzhong
 * @create 2022-04-13 15:06
 */
@Slf4j(topic = "c.Run2Wait")
public class Run2Wait {
    final static Object obj = new Object();

    public static void main(String[] args) {

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码...."); // 断点
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码...."); // 断点
            }
        },"t2").start();

        Sleeper.sleep(0.5);
        log.debug("唤醒 obj 上其它线程");

        synchronized (obj) {
            obj.notifyAll(); // 唤醒obj上所有等待线程 断点
        }
    }
}
