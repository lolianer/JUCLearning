package com.swz.chapter07;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**手写一个线程池
 *      主线程作为生产者，一直会有任务需要执行，
 *              创建一个线程池对象，调用线程池做任务  任务：（Runnable对象）
 *      线程池拿到任务以后，
 *              如果线程数没有达到最大，就需要创建线程，放到池里，调用线程的start
 *              如果已经达到最大了，就需要先进入线程池的队列中等待
 *          线程池的队列有两个主要的功能：放任务，拿任务
 *                  放任务：如果满了，就等待，如果不满，就放进去
 *                  拿任务：如果空了，就等待，如果不空，就拿
 * @author shen_wzhong
 * @create 2022-04-17 9:38
 */
@Slf4j(topic = "c.PoolTest")
public class PoolTest {
    public static void main(String[] args) {
        ThreadPool pool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1,(queue, task) -> {
            //1. 死等
//            queue.put(task);
            //2. 带超时时间
            queue.offer(task, 5000, TimeUnit.MILLISECONDS);
            //3. 让调用者放弃任务执行
//            log.debug("放弃{}",task);
            //4. 让调用者抛出异常
//            throw new RuntimeException("任务执行失败 " + task);
            //5. 让调用者自己执行任务
//            task.run(); 
            
        });
        //如果现在任务数太多，任务的队列都放不下，主线程就会等待任务执行，然后放任务，如果任务的执行时间很长，主线程就会阻塞，就需要一个拒绝策略
        for (int i = 0; i < 3; i++) {
            int j = i;
            pool.execute(() -> {
                Sleeper.sleep(10);
                log.debug("{}",j);
            });
        }
    }
}

@FunctionalInterface //拒绝策略
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}


@Slf4j(topic = "c.ThreadPool")
class ThreadPool {
    //任务队列
    private BlockingQueue<Runnable> taskQueue;

    //线程集合
    private HashSet<Worker> workers = new HashSet<>();//这里泛型不能直接是Thread，需要包装

    //核心线程数
    private int coreSize;

    //获取任务的超时时间，如果超过时间还没有任务，就停止线程
    private long timeout;

    private TimeUnit timeUnit;

    RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapcity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }

    //执行任务
    public void execute(Runnable task) {
        //当任务数没有超过 coreSize 时，直接交给 worker 对象执行
        //如果任务数超过 coreSize，就把任务加入任务队列，存起来
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{},{}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                //这里只是往任务队列中放任务，并不知道任务队列中满了没有，所以将满了之后的策略放在BlockingQueue类中完成
//                taskQueue.put(task);
                //如果线程池队列满了之后
                taskQueue.tryPut(rejectPolicy, task);
                /*
                这里有很多不同的策略，可以外放给主线程，让主线程决定使用哪种策略
                    最不好的方法：就是使用if else 用一个成员变量来决定
                    这里可以使用策略模式
                 */
                //1. 死等，直到不满
                //2. 带超时时间
                //3. 让调用者放弃任务执行
                //4. 让调用者抛出异常
                //5. 让调用者自己执行任务
                
            }
        }
    }

    //线程池中的线程集合如果直接是Thread，则信息不足，用work及进行包装
    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //执行任务
            //当创建任务的时候，直接就有任务传过来，就可以直接执行
            //1.当 task 不为空，执行任务
            //2.当 task 执行完毕，再接着从任务队列获取任务并执行
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.debug("正在执行。。。{}", task);
                    task.run();
                    log.debug("执行完毕。。。{}", task);
                } catch (Exception e) {
                    
                } finally {
                    task = null;
                }
            }
            synchronized (workers) {
                log.debug("worker 被移除{}", this);
                workers.remove(this);
            }
        }
    }
}

@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T> {
    //1.任务队列 用链表
    private Deque<T> queue = new ArrayDeque<>();

    //2.锁,多个线程从链表一端拿任务，多个线程从链表另一端放任务
    private ReentrantLock lock = new ReentrantLock();

    //两个条件变量，生产者和消费者的阻塞的地方
    //3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    //4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    //5.容量
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    //带超时的等待从队列拿任务
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            //将timeout转换为纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) {//如果剩余时间小于0，返回null
                        return null;
                    }
                    nanos = emptyWaitSet.awaitNanos(nanos);//返回值就是还剩的时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();//从队列拿取一个任务后，需要把放任务的线程唤醒
            return t;
        } finally {
            lock.unlock();
        }
    }

    //消费者从队列拿任务
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    //生产者往队列放任务
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capcity) {
                try {
                    log.debug("等待加入队列{}",task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }
    
    //带超时时间的添加任务的方法
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capcity) {
                try {
                    log.debug("等待加入队列{}",task);
                    if (nanos <= 0) {
                        log.debug("等待加入队列失败，{}", task); 
                        return false;
                    }
                    System.out.println(nanos);
                    nanos = fullWaitSet.awaitNanos(nanos);
                    System.out.println(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    //获取队列中排队执行的任务还有几个
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    //带策略的向队列中添加任务
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            //判断队列是否已满
            if (queue.size() == capcity) {
                rejectPolicy.reject(this, task);
            } else {
                log.debug("加入任务队列 {}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
