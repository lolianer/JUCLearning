package com.swz.chapter04;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shen_wzhong
 * @create 2022-04-14 14:51
 */
@Slf4j(topic = "c.Test1")
public class Test1 {
    static boolean run  = true;

    /**
     * 预想：在主程序中将run 改为false，t线程就停止，但是实际不会
     * 解决：在变量上加volatile 或者 用synchronized锁
     * @param args
     */
    static Object lock = new Object();
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (!run) {
                        break;
                    }
                }
            }
        });
        t.start();

        Sleeper.sleep(1);
        synchronized (lock) {
            run = false;
        }
        log.debug("停止t");
    }

    public static void main2(String[] args) throws InterruptedException {
        TPTVolatile t = new TPTVolatile();
        t.start();
        Thread.sleep(3500);
        log.debug("stop");
        t.stop();
    }


}

@Slf4j(topic = "c.TPTVolatile")
class TPTVolatile {
    //监控线程
    private Thread thread;
    //停止标记
    private volatile boolean stop = false;
    //判断线程是否执行过 start 方法
    private boolean starting = false;

    //启动线程
    public void start(){
        synchronized (this) {//为防止多线程同时查看starting，然后都是false，就有两个线程执行了此方法
            if (starting) {
                return;
            }
            starting = true;
        }

        thread = new Thread(() -> {
            while(true) {
                Thread current = Thread.currentThread();

                if(stop) {
                    log.debug("料理后事");
                    break;
                }

                try {
                    Thread.sleep(1000);
                    log.debug("将结果保存");
                } catch (InterruptedException e) {
                }
                // 执行监控操作
            }
        },"监控线程");
        thread.start();

    }
    public void stop() {
        stop = true;
        thread.interrupt();//让线程在休眠中立即停止而不是等待sleep结束
    }
}
