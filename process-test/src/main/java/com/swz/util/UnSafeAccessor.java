package com.swz.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**生成一个unSafe对象
 * @author shen_wzhong
 * @create 2022-04-16 10:48
 */
public class UnSafeAccessor {
    private static final Unsafe UNSAFE;

    static {
        UNSAFE = get();
    }

    public static Unsafe getSunSafe() {
        return UNSAFE;
    }

    private static Unsafe get() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
