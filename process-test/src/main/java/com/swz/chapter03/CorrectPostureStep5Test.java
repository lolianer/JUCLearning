package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**wait、notify方法的正确使用  最终阶段
 *      使用 ReentrantLock 的条件变量 防止 虚假唤醒
 * @author shen_wzhong
 * @create 2022-04-13 8:21
 */
@Slf4j(topic = "c.CorrectPostureStep5Test")
public class CorrectPostureStep5Test {
    static final Object room = new Object();
    static boolean hasCigarette = false;//有没有烟
    static boolean hasTakeout = false;

    static ReentrantLock newRoom  = new ReentrantLock();
    //等待烟的休息室
    static Condition waitCigaretteSet = newRoom.newCondition();
    //等待外卖的休息室
    static Condition waitTakeoutSet = newRoom.newCondition();


    public static void main(String[] args) {
        new Thread(() -> {
            newRoom.lock();
            try {
                log.debug("没烟，先歇会！");
                while (!hasCigarette) {
                    try {
                        waitCigaretteSet.await();
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
            } finally {
                newRoom.unlock();
            }
        }, "小南").start();

        new Thread(() -> {
            newRoom.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        waitTakeoutSet.await();
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
            } finally {
                newRoom.unlock();
            }

        }, "小女").start();

        Sleeper.sleep(1);

        new Thread(() -> {
            newRoom.lock();
            try {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                waitTakeoutSet.signalAll();
            }finally {
                newRoom.unlock();
            }
        }, "送烟的").start();

        Sleeper.sleep(1);

        new Thread(() -> {
            newRoom.lock();
            try {
                hasCigarette = true;
                log.debug("烟到了噢！");
                waitCigaretteSet.signalAll();
            }finally {
                newRoom.unlock();
            }
        }, "送烟的").start();
    }


    /**
     * 使用synchronized拿到锁之后，等待条件
     * room.wait();
     * 使用while重复等待，防止虚假唤醒
     * @param args
     */
    public static void main1(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
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
                while (!hasTakeout) {
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
                room.notifyAll(); //上面线程休眠，就必须有唤醒，否则会一直休眠
            }
        }, "送烟的").start();

    }
}
