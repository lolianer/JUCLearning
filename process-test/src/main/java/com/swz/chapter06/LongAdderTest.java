package com.swz.chapter06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * AtomicInteger 等原子整数也可以做累加，但是jdk8后新增了专门做累加的类`LongAccumulator` `LongAddr`性能高很多，所以需要专门讲解
 * @author shen_wzhong
 * @create 2022-04-15 18:03
 */
public class LongAdderTest {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            demo(
                    ()-> new AtomicLong(0),
                    (adder) -> adder.getAndIncrement()
            );
        }

        for (int i = 0; i < 5; i++) {
            demo(
                    () -> new LongAdder(),
                    adder -> adder.increment()
            );
        }

    }

    /*
    ()-> 结果  提供累加对象
    (参数)->void  执行累加操作
     */
    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        // 4 个线程，每人累加 50 万
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start)/1000_000);
    }
}

