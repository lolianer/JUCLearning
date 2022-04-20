package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shen_wzhong
 * @create 2022-04-20 9:16
 */
@Slf4j(topic = "c.CyclicBarrierTest")
public class CyclicBarrierTest {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        
        //构造器的参数是
        //人满发车的原理：第一个任务执行到await方法时，会等待，当等待的线程到了构造器的参数时，就可以唤醒之前所有等待的线程一起执行下去
        //当线程执行到了0后，这个为参数就会又是2，然后可以循环使用
        //线程池中的线程数要和计数的数是一致的：否则，就是看谁先执行完成，如果不想同步的两个线程先执行完，也会进行计数
        CyclicBarrier barrier = new CyclicBarrier(2,() -> {
            //这里是第三个任务，当前两个任务等到await的数量后，就可以执行这个任务
            log.debug("都执行完毕了");
        });

        service.submit(() -> {
            log.debug("task1 开始了");
            Sleeper.sleep(1);
            try {
                barrier.await();
                log.debug("task1 结束了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        
        service.submit(() -> {
            log.debug("task2 开始了");
            Sleeper.sleep(2);
            try {
                barrier.await();
                log.debug("task2 结束了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
    }
}
