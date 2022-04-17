package com.swz.chapter07;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**线程池的应用：定时任务
 *      如何让每周四 18:00:00 定时执行任务？
 * @author shen_wzhong
 * @create 2022-04-17 19:01
 */
public class ScheduleTest {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        
        //获取周四时间
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);
        
        //如果当前时间 > 本周周四 ， 必须找到下周周四
        if (now.compareTo(time) > 0) {
            time = time.plusWeeks(1);
        }

        //1.command 
        // 2.initialDelay 代表当前时间和周四的时间差 
        // 3. period  一周的间隔
        // 4.unit
        //一周的毫秒数
        long period = 1000 * 60 * 60 * 24 * 7;
        
        long initialDelay = Duration.between(now, time).toMillis();
        
        pool.scheduleAtFixedRate(() -> {
            System.out.println("running");
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }
}
