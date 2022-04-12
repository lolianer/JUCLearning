package com.swz.chapter04;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**偏向锁的测试
 * @author shen_wzhong
 * @create 2022-04-12 14:43
 */
@Slf4j(topic = "c.BiasedTest")
public class BiasedTest {

    public static void main1(String[] args) throws InterruptedException {
        Dog dog = new Dog();
        dog.hashCode(); //会禁用掉对象的偏量锁

        //ClassLayout类，可以打印对象的对象头
        //parseInstance，需要告诉方法要解析哪个对象
        //toPrintable(true)true,表示不会加16进制，false表示16进制，2进制都会打印
        log.debug(ClassLayout.parseInstance(dog).toPrintable());

        synchronized (dog) {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }

        log.debug(ClassLayout.parseInstance(dog).toPrintable());

        //偏向锁是默认是延迟的，不会在程序启动时立即生效,所以这里间隔4秒后，再次打印一个

        /*Thread.sleep(4000);
        String s1 = ClassLayout.parseInstance(new Dog()).toPrintable();
        log.debug(s1);*/
    }

    //测试两个不同的线程使用锁时，会将偏向锁变为不可偏向 升级为轻量级锁
    public static void main2(String[] args) {
        Dog dog = new Dog();

        new Thread(() -> {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());

            synchronized (dog) {
                log.debug(ClassLayout.parseInstance(dog).toPrintable());
            }

            log.debug(ClassLayout.parseInstance(dog).toPrintable());

            //如果不用 wait/notify 使用 join 必须 t1 线程不能结束，否则底层线程可能被 jvm 重用作为 t2 线程，底层线程 id 是一样的
            synchronized (BiasedTest.class) {
                //唤醒下面的线程
                BiasedTest.class.notify();
            }

        },"t1").start();



        new Thread(() -> {
            synchronized (BiasedTest.class) {
                try {
                    //先让进程休眠
                    BiasedTest.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug(ClassLayout.parseInstance(dog).toPrintable());

            synchronized (dog) {
                log.debug(ClassLayout.parseInstance(dog).toPrintable());
            }

            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        },"t2").start();
    }

    //批量重偏向
    public static void main3(String[] args) {
        Vector<Dog> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                //为了让这30个对象都偏向于t1
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            synchronized (list) {
                list.notify();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("===============> ");
            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                //第二个线程也要为对象加锁，于是升级成轻量级锁
                //但是，次数多了之后（默认20），就会批量重偏向，会让剩下的对象都偏向t2
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t2");
        t2.start();
    }

    static Thread t1,t2,t3;
    public static void main(String[] args) throws InterruptedException {
        Vector<Dog> list = new Vector<>();
        Dog dog = new Dog();
        int loopNumber = 39;
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
        //会把39个对象都改为偏向t1线程的偏向锁
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        //前19个对象解锁后，会改成五偏向的正常状态，后面的对象进行批量重偏向，改成了t2的偏向锁
        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        //前19个对象已经是无偏向的，解锁后还是无偏向的，后面的也会改成无偏向的
        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t3");
        t3.start();
        t3.join();
        //撤销偏向锁阈值超过 40 次，jvm新创建的对象也是无偏向的
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
    }
}

class Dog {

}
