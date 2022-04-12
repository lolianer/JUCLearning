package com.swz.chapter04;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shen_wzhong
 * @create 2022-04-12 18:34
 */
@Slf4j(topic = "c.CorrectPostureStep1Test")
public class CorrectPostureStep1Test {
    static final Object room = new Object();
    static boolean hasCigarette = false;//有没有烟
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();//这里进入休眠
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized(room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notify(); //上面线程休眠，就必须有唤醒，否则会一直休眠
            }
        }, "送烟的").start();

    }
}
