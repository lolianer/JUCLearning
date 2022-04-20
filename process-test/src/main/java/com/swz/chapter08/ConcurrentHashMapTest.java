package com.swz.chapter08;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**练习：单词计数
 * 生成测试数据
 * @author shen_wzhong
 * @create 2022-04-20 10:34
 */
public class ConcurrentHashMapTest {
    static final String ALPHA = "abcedfghijklmnopqrstuvwxyz";
    
    public static void main(String[] args) {
//        construct();
        //调用calculate方法，实现计数
        /*calculate(
                () -> new HashMap<String, Integer>(),//使用hashmap不能保证线程安全
                (map, strings) -> {//加sync当然可以，但是性能很差
                    for (String string : strings) {
                        Integer count = map.get(string);
                        int newValue = count == null ? 1 : count + 1;
                        map.put(string, newValue);
                    }
                    
                }
        );*/

        /*calculate(
                () -> new ConcurrentHashMap<String, Integer>(),//简单的使用线程安全的类并不能解决问题
                (stringMap, strings) -> {
                    for (String string : strings) {
                        Integer count = stringMap.get(string);
                        int newValue = count == null ? 1 : count + 1;
                        stringMap.put(string, newValue);
                    }
                }
        );*/

        /*calculate(
                () -> new ConcurrentHashMap<String, LongAdder>(),//简单的使用线程安全的类并不能解决问题
                (stringMap, strings) -> {
                    for (String string : strings) {                              //这里不会一直创建新对象
                        LongAdder value = stringMap.computeIfAbsent(string, s -> new LongAdder());//会返回上一次的value值
                        value.increment();
                    }
                }
        );*/
        calculate(
                () -> new ConcurrentHashMap<String, Integer>(),
                (map, words) -> {
                    for (String word : words) {
                        // 函数式编程，无需原子变量
                        map.merge(word, 1, (integer, integer2) -> Integer.sum(integer,integer2));
                    }
                }
        );
        
    }

    //开启26个线程，每个线程调用get方法获取map，从对应的文件读取单词并存储到list中，最后调用accept方法进行统计。
    private static <V> void calculate(Supplier<Map<String,V>> supplier,
                                 BiConsumer<Map<String,V>,List<String>> consumer) {
        Map<String, V> counterMap = supplier.get();//传进来一个想要的map
        List<Thread> ts = new ArrayList<>();
        
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {
                List<String> words = readFromFile(idx);//words就是一个所有的单词集合
                consumer.accept(counterMap, words);
            });
            ts.add(thread);
        }
        
//        ts.forEach(Thread::start);
        ts.forEach(t -> t.start());
        
        ts.forEach(t-> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(counterMap);
    }

    //读单词方法的实现
    public static List<String> readFromFile(int i) {
        ArrayList<String> words = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("tmp/"
                + i +".txt")))) {
            while(true) {
                String word = in.readLine();
                if(word == null) {
                    break;
                }
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //生成测试数据
    public static void construct() {
        int length = ALPHA.length();
        int count = 200;
        List<String> list = new ArrayList<>(length * count);
        for (int i = 0; i < length; i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }
        Collections.shuffle(list);//打乱顺序
        for (int i = 0; i < 26; i++) {
            try (PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("tmp\\" + (i+1) + ".txt")))) {
                String collect = list.subList(i * count, (i + 1) * count).stream()
                        .collect(Collectors.joining("\n"));
                out.print(collect);
            } catch (IOException e) {
            }
        }
    }
}
