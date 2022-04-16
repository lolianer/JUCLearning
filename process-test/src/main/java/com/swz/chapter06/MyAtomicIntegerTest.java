package com.swz.chapter06;

import com.swz.util.UnSafeAccessor;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;

/**  用unsafe实现原子整数
 * @author shen_wzhong
 * @create 2022-04-16 10:45
 */
public class MyAtomicIntegerTest {
    public static void main(String[] args) {

        Account1 account = new Account1(10000);

        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()
                + " cost: " + (end-start)/1000_000 + " ms");
    }
}
//具体的账户类
class Account1{
    private MyAtomicInteger balance;

    public Account1(int balance) {
        this.balance = new MyAtomicInteger(balance);
    }

    public Integer getBalance() {
        return balance.getValue();
    }

    public void withdraw(Integer amount) {
        balance.decrement(amount);
    }
}
//自定义的AtomicInteger类，用于原子性操作账户
class MyAtomicInteger {
    private volatile int  value;//保护的整型变量

    private static final long valueOffset;//整型变量在这个类中的偏移量
    private static final Unsafe UNSAFE;

    static {
        UNSAFE = UnSafeAccessor.getSunSafe();
        try {
            valueOffset = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    //账户减钱
    public void decrement(int amount) {
        while (true) {
            int prev = this.value;
            int next = prev - amount;
            //保护的整形变量是int时，可以用Int，如果时Integer，必须用Object
            if (UNSAFE.compareAndSwapInt(this, valueOffset, prev, next)) {
                break;
            }
        }

    }

}
