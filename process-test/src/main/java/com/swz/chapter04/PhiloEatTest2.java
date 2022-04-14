package com.swz.chapter04;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 哲学家就餐问题
 *      用tryLock()超时等待解决
 * @author shen_wzhong
 * @create 2022-04-14 8:18
 */
public class PhiloEatTest2 {
    public static void main(String[] args) {
        Chopstick1 c1 = new Chopstick1("1");
        Chopstick1 c2 = new Chopstick1("2");
        Chopstick1 c3 = new Chopstick1("3");
        Chopstick1 c4 = new Chopstick1("4");
        Chopstick1 c5 = new Chopstick1("5");
        new Philosopher1("苏格拉底", c1, c2).start();
        new Philosopher1("柏拉图", c2, c3).start();
        new Philosopher1("亚里士多德", c3, c4).start();
        new Philosopher1("赫拉克利特", c4, c5).start();
        new Philosopher1("阿基米德", c5, c1).start();
    }
}

@Slf4j(topic = "c.Chopstick")
class Chopstick1 extends ReentrantLock {//把筷子当成锁对象
    String name;
    public Chopstick1(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}

@Slf4j(topic = "c.Philosopher")
class Philosopher1 extends Thread {
    Chopstick1 left;
    Chopstick1 right;

    public Philosopher1(String name, Chopstick1 left, Chopstick1 right) {
        super(name);
        this.left = left;
        this.right = right;
    }
    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(1);
    }
    @Override
    public void run() {
        while (true) {
            // 获得左手筷子
            if (left.tryLock()) {
                try {
                    // 获得右手筷子
                    if (right.tryLock())//拿不到就不执行下面的语句，直接进外层的finally
                        try {
                            // 吃饭
                            eat();
                        } finally {
                            // 放下右手筷子
                            right.unlock();
                        }
                } finally {
                    // 放下左手筷子
                    left.unlock();
                }
            }
        }
    }
}
