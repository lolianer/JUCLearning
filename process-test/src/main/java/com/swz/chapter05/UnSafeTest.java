package com.swz.chapter05;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/** 用更底层的unsafe实现字段更新
 * @author shen_wzhong
 * @create 2022-04-16 10:22
 */
public class UnSafeTest {
    public static void main(String[] args) {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unSafe = (Unsafe) theUnsafe.get(null);

            System.out.println(unSafe);

            //1.获取域的偏移地址
            long idOffset = unSafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
            long nameOffset = unSafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

            Teacher teacher = new Teacher();

            //2.执行cas操作
            //1：操作的对象，2.操作的对象的域的偏移量,3.获取时的旧值，4.要修改的新值
            unSafe.compareAndSwapInt(teacher, idOffset, 0, 1);
            unSafe.compareAndSwapObject(teacher, nameOffset, null, "张三");

            //3.验证
            System.out.println(teacher);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

@Data
class Teacher {
    volatile int id;
    volatile String name;
}
