package com.swz.chapter07;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**Fork/Join线程池
 *      求1~n之间整数的和
 * @author shen_wzhong
 * @create 2022-04-18 9:37
 */
@Slf4j(topic = "c.ForkJoinTest")
public class ForkJoinTest {
    public static void main(String[] args) {
        //创建Fork/join线程池对象
        ForkJoinPool pool = new ForkJoinPool();
        Integer invoke = pool.invoke(new AddTask3(1, 1000));
        System.out.println(invoke);
        
        //如何拆分 从1加到5 的任务
        //new MyTask(5) = 5 + myTask(4) = 4 + myTask(3) ...
    }
    
}

//任务对象，不能再用runnable callable 对象
@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Integer> {//如果有返回值，继承RecursiveTask，如果没有返回值，继承RecursiveAction
    //利用任务拆分思想解决求和问题
    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        //终止条件 // 如果 n 已经为 1，可以求得结果了
        if (n == 1) {
            log.debug("join() {}", n);
            return 1;
        }

        // 将任务进行拆分(fork)
        MyTask t1 = new MyTask(n - 1);
        t1.fork();//让一个线程去执行这个任务
        log.debug("fork() {} + {}", n, t1);

        // 合并(join)结果
        int result = n + t1.join();// 获取任务结果
        log.debug("join() {} + {} = {}", n, t1, result);
        return result;
    }

    @Override
    public String toString() {
        return "{" + n + '}';
    }
}

@Slf4j(topic = "c.AddTask3")
class AddTask3 extends RecursiveTask<Integer> {
    //代表总任务的起始和结束 1,5
    int begin;
    int end;
    
    public AddTask3(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return "{" + begin + "," + end + '}';
    }
    
    @Override
    protected Integer compute() {
        // 5, 5，如果拆分到没有中间的数字，就不需要拆分，直接求出
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        // 4, 5
        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }

        // 1 5
        int mid = (end + begin) / 2; // 3
        
        AddTask3 t1 = new AddTask3(begin, mid); // 1,3
        t1.fork();
        AddTask3 t2 = new AddTask3(mid + 1, end); // 4,5
        t2.fork();
        
        log.debug("fork() {} + {} = ?", t1, t2);
        int result = t1.join() + t2.join();
        log.debug("join() {} + {} = {}", t1, t2, result);
        return result;
    }
}