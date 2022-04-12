package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

/**测试调用start和run方法
 * @author shen_wzhong
 * @create 2022-04-11 10:58
 */
@Slf4j(topic = "c.ThreadTest2")
public class ThreadTest2 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1");

        log.debug("t1 state:{}",thread.getState());//新创建出来的线程状态是NEW
        thread.start();
        log.debug("t1 state:{}",thread.getState());//start以后状态是RUNNABLE

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("t1 state:{}",thread.getState());//sleep之后状态是TIMED_WAITING
    }
}
