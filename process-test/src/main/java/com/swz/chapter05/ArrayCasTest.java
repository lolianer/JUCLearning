package com.swz.chapter05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 如果我们要修改的不是对象本身，而是对象内的某个属性，比如数组，不需要换一个新的数组，而是修改数组内的某一个值，就用原子数组，保护数组内的元素的线程安全
 * @author shen_wzhong
 * @create 2022-04-15 16:15
 */
public class ArrayCasTest {

    //main方法和demo方法
    // main中的使用demo方法的参数有4个，都是函数是接口，
    // demo方法的4个参数，在demo方法用到，然后传参，然后在使用时进行定义这4个参数，每个方法还都有参数，这个参数是demo方法中给定了的

    //在使用给定的demo方法时，发现4个参数都是方法，用lambda表达式，这4个表达式的参数是对方给的，所以需要到定义的方法中看传的参数是什么，才能在lambda表达式中正确使用
    public static void main(String[] args) {
        //这里是在使用下面定义的方法，所以，这4个方法的参数是定了的
        demo(
                ()->new int[10],
                (array) -> array.length,
                (array, index) -> array[index]++,
                array -> System.out.println(Arrays.toString(array))
        );

        demo(
                () -> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                array -> System.out.println(array)
        );
    }


    /**
     * @param arraySupplier 参数1，提供数组、可以是线程不安全数组或线程安全数组
     * @param lengthFun 参数2，获取数组长度的方法
     * @param putConsumer 参数3，自增方法，回传array，index
     * @param printConsumer 参数4，打印数组的方法
     * @param <T>
     */
    // supplier 提供者 无中生有 ()->结果
    // function 函数  需要一个参数，返回一个结果  (参数)->结果     ， BiFunction(参数1,参数2)->结果
    // consumer 消费者 一个参数没有结果  (参数)->{void},     BiConsumer(参数1,参数2)->{void}
    private static <T> void demo(Supplier<T> arraySupplier,
                                 Function<T, Integer> lengthFun,
                                 BiConsumer<T, Integer> putConsumer,
                                 Consumer<T> printConsumer) {
        List<Thread> ts = new ArrayList<>();
        //arraySupplier 是一个方法，提供了一个数组
        T array = arraySupplier.get();
        //lengthFun 是获取长度的方法
        Integer length = lengthFun.apply(array);

        for (int i = 0; i <length; i++) {
            //每个线程对数组做 10000 此操作
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    //打印数组的方法
                    putConsumer.accept(array, j % length);
                }
            }));
        }

        ts.forEach(t -> t.start()); // 启动所有线程
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }); // 等所有线程结束

        printConsumer.accept(array);
    }


    //将上面的方法进行比对，插入以后：
    private static void demo2() {
        List<Thread> ts = new ArrayList<>();
        //AtomicIntegerArray只需要一个参数，长度
        AtomicIntegerArray array = new AtomicIntegerArray(10);
        //lengthFun 是获取长度的方法
        Integer length = array.length();

        for (int i = 0; i <length; i++) {
            //每个线程对数组做 10000 此操作
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    //打印数组的方法
                    array.getAndIncrement(j % length);
                    int i1 = array.get(1);
//                    array.compareAndSet()
                }
            }));
        }

        ts.forEach(t -> t.start()); // 启动所有线程
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }); // 等所有线程结束

        System.out.println(array);
    }
}
