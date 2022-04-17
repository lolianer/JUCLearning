package com.swz.chapter03;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**测试消息队列  生产者消费者
 *      生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
 * @author shen_wzhong
 * @create 2022-04-13 11:26
 */

@Slf4j(topic = "c.MessageQueueTest")
public class MessageQueueTest {

    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id, "值" + id));
            },"生产者" + i).start();
        }

        new Thread(() -> {
            while (true) {
                Sleeper.sleep(1);
                Message message = queue.take();
            }
        },"消费者").start();
    }
}

//消息队列类，java线程之间通信
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    //创建一个集合，用来存储消息
    private LinkedList<Message> list = new LinkedList<>();//这里使用双向链表，从一边存，从另一边取
    //队列容量，不能无限制往容器中放消息
    private int capcity;

    public MessageQueue(int capcity) {
        this.capcity = capcity;
    }

    //获取消息
    public Message take() {
        //检查队列是否为空
        synchronized (list) {
            //唤醒后，查看是否为空
            while (list.isEmpty()) {
                try {
                    log.debug("队列为空，消费者线程只能等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //不为空时，返回队列头部消息
            Message message = list.removeFirst();
            log.debug("以消费消息，{}",message);
            list.notify();
            return message;
        }

    }
    //存入消息
    public void put(Message message) {
        synchronized (list) {
            //检查队列是否已满
            while (list.size() == capcity) {
                try {
                    log.debug("队列已满，生产者线程只能等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //将消息加入队列尾部
            list.addLast(message);
            log.debug("以生产消息，{}",message);
            list.notify();
        }
    }
}
/**
 * 进程之间通信用的消息是用什么类型？需要用id来联系各个线程
 *
 * 为了线程安全，不能修改成员变量，只能一次性赋值，而且final类，无法继承
 */
final class Message {
    private int id;
    private Object val;

    public Message(int id, Object val) {
        this.id = id;
        this.val = val;
    }

    public int getId() {
        return id;
    }

    public Object getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", val=" + val +
                '}';
    }
}