package com.swz.chapter03;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shen_wzhong
 * @create 2022-04-12 10:40
 */
@Slf4j(topic = "c.ExerciseTransfer")
public class ExerciseTransfer {
    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                a.transfer(b, 100);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
//                a.transfer(b, 100);
                b.transfer(a, 100);
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 查看转账2000次后的总金额
        log.debug("total:{}",(a.getMoney() + b.getMoney()));
        log.debug("a.getMoney():{}",(a.getMoney()));
        log.debug(" b.getMoney():{}",(b.getMoney()));
    }

}
class Account {
    private int money;
    public Account(int money) {
        this.money = money;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void transfer(Account target, int amount) {
        synchronized (Account.class) {
            if (this.money >= amount) {
                this.setMoney(this.getMoney() - amount);
                target.setMoney(target.getMoney() + amount);
            }
        }

    }
}
