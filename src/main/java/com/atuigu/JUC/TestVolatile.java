package com.atuigu.JUC;

/**
 * 场景---两个线程，一个线程对当前数值加 1，另一个线程对当前数值减 1,要求用线程间通信
 * volatile 关键字实现线程交替加减
 * @author shen_wzhong
 * @create 2022-04-10 18:47
 */
public class TestVolatile {
    public static void main(String[] args){
        DemoClass demoClass = new DemoClass();
        new Thread(() ->{
            for (int i = 0; i < 5; i++) {
                demoClass.increment();
            }
        }, "线程 A").start();
        new Thread(() ->{
            for (int i = 0; i < 5; i++) {
                demoClass.decrement();
            }
        }, "线程 B").start();
    }
}
class DemoClass{
    //加减对象
    private int number = 0;
    /**
     * 加 1
     */
    public synchronized void increment() {
        try {
            while (number != 0){
                this.wait();
            }
            number++;
            System.out.println("--------" + Thread.currentThread().getName() + "加一成 功----------,值为:" + number);
            notifyAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 减一
     */
    public synchronized void decrement(){
        try {
            while (number == 0){
                this.wait();
            }
            number--;
            System.out.println("--------" + Thread.currentThread().getName() + "减一成 功----------,值为:" + number);
            notifyAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
