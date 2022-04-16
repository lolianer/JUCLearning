package com.swz.chapter07;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * 下面的代码在运行时，由于 SimpleDateFormat 不是线程安全的
 * 可变类在多线程时会发生错误
 * @author shen_wzhong
 * @create 2022-04-16 15:22
 */
@Slf4j(topic = "c.SimpleDateFormatTest")
public class SimpleDateFormatTest {

    public static void main(String[] args) {
        Integer i = Integer.valueOf(128);
        Integer j = 128;
        System.out.println(i == j);
    }


    //不可变的类是线程安全的
    public static void main2(String[] args) {
        DateTimeFormatter stf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                TemporalAccessor parse = stf.parse("1951-04-21");
                log.debug("{}",parse);
            }).start();
        }
    }


    public static void main1(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (sdf) {
                    try {
                        log.debug("{}", sdf.parse("1951-04-21"));
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }

            }).start();
        }
    }
}
