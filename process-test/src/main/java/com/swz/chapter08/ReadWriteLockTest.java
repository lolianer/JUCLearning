package com.swz.chapter08;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author shen_wzhong
 * @create 2022-04-18 17:12
 */
@Slf4j(topic = "c.ReadWriteLockTest")
public class ReadWriteLockTest {
    public static void main(String[] args) {
        DataContainer dataContainer = new DataContainer();
        
        new Thread(() -> {
            dataContainer.read();
        }, "t1").start();
        
        Sleeper.sleep(1);
        new Thread(() -> {
            dataContainer.write();
        }, "t2").start();
    }
}

@Slf4j(topic = "c.DataContainer")
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();
    
    public Object read() {
        log.debug("获取读锁");
        r.lock();
        try {
            log.debug("读取");
            Sleeper.sleep(1);
            return data;
        } finally {
            log.debug("释放读锁");
            r.unlock();
        }
    }
    
    public void write() {
        log.debug("获取写锁");
        w.lock();
        try {
            log.debug("写入");
            Sleeper.sleep(1);
        } finally {
            log.debug("释放写锁");
            w.unlock();
        }

    }
}
