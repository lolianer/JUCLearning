package com.swz.chapter06;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * @author shen_wzhong
 * @create 2022-04-15 14:28
 */
public class AtomicIntegerTest {
    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(5);

//        i.compareAndSet(int expect, int update);//如果i对象维护的值和第一个参数的值相等，对象就改成第二个参数的值
        /*System.out.println(i.incrementAndGet());//自增并获取值，相当于++i 1
        System.out.println(i.getAndIncrement());//获取值并自增，相当于i++ 2
//        i.decrementAndGet();//--i
//        i.getAndDecrement();//i--

        System.out.println(i.getAndAdd(5));//7
        System.out.println(i.addAndGet(5));//12*/

//        i.updateAndGet(value -> value * 10);

        //接下来自定义一个updateAndGet方法
        /*while (true) {
            int prev = i.get();
            int next = prev * 10;
            if (i.compareAndSet(prev, next)) {
                break;
            }
        }*/

        //变得有通用性
        updateAndSet(i, p -> p / 2);

        System.out.println(i.get());
    }

    public static void updateAndSet(AtomicInteger i, IntUnaryOperator operator) {
        while (true) {
            int prev = i.get();
            int next = operator.applyAsInt(prev);
            if (i.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}
