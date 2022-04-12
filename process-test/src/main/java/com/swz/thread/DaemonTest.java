package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

import static com.swz.util.Sleeper.sleep;

/**测试守护线程
 * @author shen_wzhong
 * @create 2022-04-11 15:46
 */
@Slf4j(topic = "c.DaemonTest")
public class DaemonTest {
    public static void main(String[] args) {
        Thread t = new Thread(()->{
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.debug("结束");
        },"t1") ;


        // 设置该线程为守护线程
        t.setDaemon(true);

        t.start();
//        sleep(1);
        log.debug("结束");
    }

    int i =1;
    static int j = 2;
    public void method() {
        int k = 1;
        k++;
        i++;
        j++;
    }
}
