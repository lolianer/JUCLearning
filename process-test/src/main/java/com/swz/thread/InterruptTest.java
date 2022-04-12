package com.swz.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

/**测试interrupt方法，
 * 将线程在睡眠中打断，会报InterruptedException异常
 * 在正常运行中打断，只会给线程一个标记
 * @author shen_wzhong
 * @create 2022-04-11 11:14
 */
@Slf4j(topic = "c.ThreadTest2")
public class InterruptTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            log.debug("enter sleep...");
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                log.debug("wake up...");
                e.printStackTrace();
            }
        },"t1");
        thread.start();
        try {
            sleep(1000);
            log.debug("interrupt...");
            thread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        Thread t1 = new Thread(()->{
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");


        t1.start();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
        log.debug(" 打断状态: {}", t1.isInterrupted());
    }

    @Test
    public void test2(){
        Thread t2 = new Thread(()->{
            while(true) {
                Thread current = Thread.currentThread();
                boolean interrupted = current.isInterrupted();
                if(interrupted) {
                    log.debug(" 打断状态: {}", interrupted);
                    break;
                }
            }
        }, "t2");
        t2.start();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.interrupt();
    }

}
