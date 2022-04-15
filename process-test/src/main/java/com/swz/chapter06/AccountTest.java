package com.swz.chapter06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**有如下需求，保证 account.withdraw 取款方法的线程安全
 *
 * @author shen_wzhong
 * @create 2022-04-15 11:16
 */
public class AccountTest {
    public static void main(String[] args) {
        Account account = new AccountCas(10000);
        Account.demo(account);
    }

}
class AccountCas implements Account {

    //使用AtomicInteger，原子整数
    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);//将普通的int转换为原子整数
    }

    @Override
    public Integer getBalance() {
        return balance.get();//将原子整数转换为普通的int
    }

    /**
     *在线程内声明的变量，没有逃逸，会进行栈上分配
     * @param amount
     */
    @Override
    public void withdraw(Integer amount) {
        while (true) {//这里使用while (true)一直循环，防止没有修改成功，就不修改了，这里就让他一直修改，直到修改成功，cas
            //获取余额的最新值
            int prev = balance.get();
            //要修改的余额
            int next = prev - amount;
            //真正修改
            //用prev值和要修改的值进行比对，如果还是，就说明在之前的获取到现在的修改之间没有别的线程动过，就可以修改成功，
            // 如果prev值和现在的balance的真正的值不一样了,说明在之前的获取到现在有别的线程动过了值，那么修改失败
            if (balance.compareAndSet(prev, next)) {
                break;
            }
            // 可以简化为下面的方法
            // balance.addAndGet(-1 * amount);
        }
    }
}

/**
 * 如果默认不加sync的话，是线程不安全的
 * 加了sync，是重量级锁，所有的线程都会顺序执行
 */
class AccountUnSafe implements Account {
    private Integer balance;

    public AccountUnSafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return this.balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }
}

interface Account {
    // 获取余额
    Integer getBalance();
    // 取款
    void withdraw(Integer amount);
    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1; i++) {
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
