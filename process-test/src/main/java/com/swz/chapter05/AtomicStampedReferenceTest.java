package com.swz.chapter05;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 解决ABA问题，当线程用之前查到的数据进行乐观锁校验时，发现一致，就是正确的吗？
 *      有可能别的线程已经改动，然后再次改了回来，所以要加上版本号
 * @author shen_wzhong
 * @create 2022-04-15 15:41
 */
@Slf4j(topic = "c.AtomicStampedReferenceTest")
public class AtomicStampedReferenceTest {
    //构造方法第一个参数是初始的值，第二个参数是初始的版本号
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("版本 {}", stamp);
        // 如果中间有其它线程干扰，发生了 ABA 现象
        other();
        Sleeper.sleep(1);
        // 尝试改为 C
        log.debug("change A->C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));
    }

    private static void other() {

        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t1").start();
        Sleeper.sleep(0.5);

        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t2").start();
    }
}
