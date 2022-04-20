package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  实现不可重入锁
 * @author shen_wzhong
 * @create 2022-04-18 10:39
 */
@Slf4j(topic = "c.AqsTest")
public class AqsTest {
    public static void main(String[] args) {
        MyLock myLock = new MyLock();
        ReentrantLock lock = new ReentrantLock();
        /*new Thread(() -> {
            myLock.lock();
            myLock.lock();
            try {
                log.debug("locking...");
                Sleeper.sleep(1);
            } finally {
                log.debug("unlocking...");
                myLock.unlock();
            }
        },"t1").start();*/
        
        /*new Thread(() -> {
            lock.lock();
            lock.lock();
            try {
                log.debug("locking...");
            } finally {
                log.debug("unlocking...");
                myLock.unlock();
            }
        },"t2").start();*/
    }
} 

//自定义锁（不可重入锁）
class MyLock implements Lock {
    
    //独占锁  同步器类
    //这个锁实现的大部分功能都是由这个类完成的
    class MySync extends AbstractQueuedSynchronizer {
        @Override //尝试获取锁
        protected boolean tryAcquire(int arg) {//如果是可重入锁，就需要用到arg做技术操作
            //定义，0是未加锁，从0变成1，就加锁
            if (compareAndSetState(0, 1)) {
                //加上了锁，并设置 owner 为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override//尝试释放锁
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);//表示没有线程占用锁
            //这行需要在下面，state由volatile修饰，写屏障可以让上面的操作都在主内存
            setState(0);//释放锁时是没有竞争的
            return true;
        }

        @Override //是否当前线程是否占有锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
        
        public Condition newCondition() {
            return new ConditionObject();//这个也是AbstractQueuedSynchronizer中的内部类
        }
    }
    
    private MySync sync = new MySync();

    @Override //加锁，如果不成功，就等待，不可打断
    public void lock() {
        //如果第一次tryAcquire不成功，会进入等待队列
        sync.acquire(1);//调用MySync成员变量的加锁
    }

    @Override //加锁，等待时可打断锁
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override //尝试加锁，成功与否都会立即返回结果
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override   //带超时时间的尝试加锁 
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override //解锁
    public void unlock() {
        //这里会调用tryRelease，然后会唤醒等待锁的其他线程
        sync.release(1);
    }

    @Override //创建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }
}
    