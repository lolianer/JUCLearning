package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

/**提供一个`数据容器类`内部分别使用读锁保护数据的`read()`方法，写锁保护数据的`write()`方法
 * @author shen_wzhong
 * @create 2022-04-19 10:47
 */
public class StampedLockTest {
    public static void main(String[] args) {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(() -> {
            dataContainer.read(1);
        }, "t1").start();
        
        Sleeper.sleep(0.5);
        
        new Thread(() -> {
            dataContainer.write(1000);
        }, "t2").start();
    }
}

@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped {
    private int data;//保护的数据 
    private final StampedLock lock = new StampedLock();
    
    public DataContainerStamped(int data) {
        this.data = data;
    }
    
    public int read(int readTime) {
        //获取戳
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        //读取数据
        Sleeper.sleep(readTime);
        //读取数据之后再验戳
        if (lock.validate(stamp)) {
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }
        //如果验戳失败，说明已经数据已经被修改，需要升级锁重新读。
        // 锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = lock.readLock();
            log.debug("read lock {}", stamp);
            Sleeper.sleep(readTime);
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        } finally {
            log.debug("read unlock {}", stamp);
            lock.unlockRead(stamp);
        }
    }
    
    public void write(int newData) {
        long stamp = lock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            Sleeper.sleep(2);
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            lock.unlockWrite(stamp);
        }
    }
}
