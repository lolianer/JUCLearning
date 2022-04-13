package com.swz.chapter04;

import com.swz.util.Downloader;
import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**测试GuardedObject保护性暂停
 * 一个线程等待另一个线程的执行结果,用Guarded 在中间调节
 * @author shen_wzhong
 * @create 2022-04-13 8:31
 */
@Slf4j(topic = "c.GuardedObjectTest")
public class GuardedObjectTest {
    //测试GuardedObject保护性暂停，线程同步
    public static void main1(String[] args) {
        //线程1等待线程2的下载结果
        GuardedObject guardedObject = new GuardedObject();

        new Thread(() -> {
            //等待结果
            log.debug("等待结果");
            List<String> list = (List<String>) guardedObject.get();
            log.debug("结果大小：{}",list.size());
        },"t1").start();

        new Thread(() -> {
            log.debug("执行下载");
            List<String> list = Downloader.download();
            guardedObject.complete(list);//之后还可以干别的事，并没有占用锁
        },"t2").start();
    }

    //测试带最大暂停时间的GuardedObject
    public static void main(String[] args) {
        //线程1等待线程2的下载结果
        GuardedObject guardedObject = new GuardedObject();

        new Thread(() -> {
            //等待结果
            log.debug("等待结果");
            Object response = guardedObject.get(2000);
            log.debug("结果是：{}",response);
        },"t1").start();

        new Thread(() -> {
            log.debug("执行下载");
            Sleeper.sleep(1);
            guardedObject.complete(null);//之后还可以干别的事，并没有占用锁
        },"t2").start();
    }

}

class GuardedObject {
    //结果
    private Object response;

    //获取结果，带超时时间，单位是毫秒
    public Object get(long timeout) {
        synchronized (this) {
            long begin = System.currentTimeMillis();//开始等待的时间
            long passedTime = 0;//已经等待的时间
            while (response == null) {//循环防止虚假唤醒
                long waitTime = timeout - passedTime;//这一轮应该等待的时间
                if (waitTime < 0) {
                    break;//通过控制等待时间是否为0，让等待停止，如果没有，就出不去了
                }
                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - begin;
            }

            return response;
        }
    }

    //获取结果
    public Object get() {
        synchronized (this) {
            //没有结果就一直等待
            while (response == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return response;
        }
    }

    //产生结果
    public void complete(Object response) {
        synchronized (this) {
            //给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}
