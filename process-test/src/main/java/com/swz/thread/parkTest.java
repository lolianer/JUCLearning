package com.swz.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

import static com.swz.util.Sleeper.sleep;

/**打断 park 线程, 不会清空打断状态
 * @author shen_wzhong
 * @create 2022-04-11 14:54
 */
@Slf4j(topic = "c.parkTest")
public class parkTest {

    @Test
    public void test3(){
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
            log.debug("打断状态：{}", Thread.currentThread().isInterrupted());

            LockSupport.park();
            log.debug("unpark...");
        }, "t1");
        t1.start();

        sleep(1);
        t1.interrupt();
    }
}

