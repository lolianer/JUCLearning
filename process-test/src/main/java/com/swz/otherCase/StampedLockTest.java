package com.swz.otherCase;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

/**
 *  用StampedLock去乐观的读
 *      StampedLock和ReadWriteLock相比，改进之处在于：`读的过程中也允许获取写锁后写入` 。
 *      这样一来，我们读的数据就可能不一致，所以，**需要一点额外的代码来判断读的过程中是否有写入，这种读锁是一种乐观锁**。
 * @author shen_wzhong
 * @create 2022-04-21 18:00
 */
public class StampedLockTest {
    private static final StampedLock stampedLock = new StampedLock();

    private static Integer DATA = 0;

    public static void write() {
        long stamp = -1;
        try {
            stamp = stampedLock.writeLock();// 获取写锁
            DATA++;
            System.out.println("写-->" + DATA);
        } finally {
            stampedLock.unlockWrite(stamp); // 释放写锁
        }
    }

    /*public static void read() {
        long stamp = stampedLock.tryOptimisticRead(); // 获得一个乐观读锁

        //在这块可能会有写锁抢锁，修改数据，所以用validate检查乐观读锁后是否有其他写锁发生
        *//**
         * 1、在这块可能会有写锁抢锁，修改数据，所以用validate检查乐观读锁后是否有其他写锁发生
         * 判断执行读操作期间,是否存在写操作,如果存在则validate返回false
         * 2、如果有写锁抢锁，修改了数据，那么就要获取悲观锁。因为写锁在修改数据的过程中，你不能直接
         * 去读，只能老老实实拿到读锁再去读，才不会发生线程安全问题
         *//*
        if (!stampedLock.validate(stamp)) {//检查乐观读锁后是否有其他写锁发生
            try {
                stamp = stampedLock.readLock();// 获取悲观读锁
                System.out.println("悲观读-->" + DATA);
                return;
            } finally {
                stampedLock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        System.out.println("乐观读-->" + DATA);

    }*/

    public static void read() {
        long stamp = -1;

        try {
            stamp = stampedLock.readLock();// 获取悲观读锁
            System.out.println("读-->" + DATA);
        } finally {
            stampedLock.unlockRead(stamp); // 释放悲观读锁
        }
    }

    public static void main1(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //写任务
        Runnable writeTask = () -> {
            for (; ; ) {
                write();
            }
        };
        //读任务
        Runnable readTask = () -> {
            for (; ; ) {
                read();
            }
        };

        //一个线程写，9个线程读
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(readTask);
        executor.submit(writeTask);


    }

    public static void main(String[] args) {
        try {
            /*URLConnection conn = new URL("https://www.lolian.xyz/").openConnection();
            List<String> lines = new ArrayList<>();
            //try-with-resources机制
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }*/
            // 1.客户端要请求于服务端的socket管道连接。
            Socket socket = new Socket("google.com", 8080);
            // 2.从socket通信管道中得到一个字节输出流
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            if((line = br.readLine()) != null){
                System.out.println(line);
            }
            System.out.println("客户端发送完毕~~~~");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
