package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**Park和Unpark方法的测试
 * @author shen_wzhong
 * @create 2022-04-13 14:32
 */
@Slf4j(topic = "c.ParkUnparkTest")
public class ParkUnparkTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            Sleeper.sleep(1);
            log.debug("park...");
            LockSupport.park();
            log.debug("resume");
        },"t1");
        t1.start();

        Sleeper.sleep(2);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }
}
