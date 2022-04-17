package com.swz.chapter07;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**可以使用 java.util.Timer 来实现定时功能
 * @author shen_wzhong
 * @create 2022-04-17 17:56
 */
@Slf4j(topic = "c.TimerTest")
public class TimerTest {
    public static void main1(String[] args) {
        Timer timer = new Timer();
        
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
//                Sleeper.sleep(2);
//                int i = 1 / 0;
            }
        };
        
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };
        
        // 使用 timer 添加两个任务，希望它们都在 1s 后执行
        // 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此『任务1』的延时，影响了『任务2』的执行
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }

    public static void main2(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        
        pool.schedule(()-> {
            log.debug("task1");
//            Sleeper.sleep(2);
            int i = 1 / 0;//如果有异常，控制台是不打印的
        },1, TimeUnit.SECONDS); 
        
        pool.schedule(()-> {
            log.debug("task2");
        },1, TimeUnit.SECONDS); 
    }

    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start....");
        //每隔1秒就执行一遍任务
        /**
         * 1. 任务对象 2.延时时间 3.执行间隔（从上一次任务开始执行开始） 4.时间单位
         */
        /*pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
            Sleeper.sleep(5);//如果任务执行时间很长，线程池不会重叠执行，线程池按照两者的大的开始下一个任务
        }, 1, 5, TimeUnit.SECONDS);*/
        
        /**
         * 1. 任务对象 2.延时时间 3.执行间隔(从上一次任务执行完毕开始) 4.时间单位
         */
        pool.scheduleWithFixedDelay(() -> {
            log.debug("running...");
            Sleeper.sleep(2);//如果任务执行时间很长，下个任务从上一个执行完毕开始计算时间
        },1, 2, TimeUnit.SECONDS);
    }
}
