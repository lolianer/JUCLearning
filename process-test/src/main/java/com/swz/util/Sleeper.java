package com.swz.util;

import java.math.BigDecimal;
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

    public static void sleep(double i) {
        try {
            BigDecimal k = BigDecimal.valueOf(i);
            BigDecimal multiply = k.multiply(new BigDecimal(1000));
            long time = multiply.longValue();
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
