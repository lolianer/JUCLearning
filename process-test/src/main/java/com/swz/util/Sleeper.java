package com.swz.util;

import java.util.concurrent.TimeUnit;

/** 工具类，对经常用的代码进行浓缩
 * @author shen_wzhong
 * @create 2022-04-11 14:57
 */
public class Sleeper {
    /**
     * 线程休眠多长时间
     * @param i
     */
    public static void sleep(long i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
