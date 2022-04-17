package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**多任务版**GuardedObject
 *      左侧的 t0，t2，t4 就好比等待邮件的居民，右侧的 t1，t3，t5就好比邮递员。
 *      自己的改动，让在people内生成的guardedObject，改到了在主线程中生成
 * @author shen_wzhong
 * @create 2022-04-13 10:21
 */
@Slf4j(topic = "c.GuardedObjectTest")
public class GuardedObjectTest2 {
    public static void main(String[] args) {

        for (int i = 0; i < 3; i++) {
            GuardedObject1 guardedObject = MailBoxes.createGuardedObject();
            new People(guardedObject.getId()).start();
        }
        Sleeper.sleep(1);
        for (Integer id : MailBoxes.getIds()) {
            new PostMan(id,"内容" + id).start();
        }
    }

}

/**
 * 每个People对应信箱的中每个GuardedObject1，需要有自己的标识来对应
 */
@Slf4j(topic = "c.People")
class People extends Thread{
    private int id;

    public People(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        //收信
        GuardedObject1 guardedObject = MailBoxes.getGuardedObject(id);
        MailBoxes.addGuardedObject(guardedObject);
        log.debug("开始收信，id：{}",guardedObject.getId());
        Object mail = guardedObject.get(5000);//拿到guardedObject，进行等待，最大等待5秒
        log.debug("收到信 id：{}，内容：{}",guardedObject.getId(),mail);
    }
}

@Slf4j(topic = "c.PostMan")
class PostMan extends Thread{
    private int id;
    private String mail;

    public PostMan(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        //寄信
        GuardedObject1 guardedObject = MailBoxes.getGuardedObject(id);
        log.debug("开始送信，id：{}，内容是：{}",id,mail);
        guardedObject.complete(mail);//和上面一样，拿到guardedObject，开始送信，唤醒people线程
    }
}
//邮箱的总体
class MailBoxes {
    private static Map<Integer,GuardedObject1> boxes = new Hashtable<>();//包含着所有的GuardedObject对象

    private static int id = 1;
    //产生唯一的id
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObject1 getGuardedObject(int id) {//根据id获取map中的对象，因为是一次性的，所以要及时删除
        return boxes.remove(id);
    }
    public static void addGuardedObject(GuardedObject1 go) {//但是，改动后在people也要获取，就删除后再添加回来
        boxes.put(go.getId(),go);
    }

    public static GuardedObject1 createGuardedObject() {//在MailBoxes类中进行创建，在people创建不能保证id唯一
        GuardedObject1 go = new GuardedObject1(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {//拿到map所有的id，用来获取其中的guardedObject
        return boxes.keySet();
    }
}
//相当于邮箱中的每一个小格子，对应每个居民
class GuardedObject1 {
    //每个对象的唯一标识，区分不同的对象
    private int id;

    public GuardedObject1(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

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
