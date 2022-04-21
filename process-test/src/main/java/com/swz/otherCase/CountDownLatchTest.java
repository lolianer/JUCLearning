package com.swz.otherCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 给离散的平行任务增加逻辑层次关系
 *
 * 需求
 * 并发的从很多的数据库读取大量数据
 * 在读取数据的过程中，某个表可能会出现： 数据丢失、数据精度丢失、数据大小不匹配。
 * 需要进行对数据的各个情况进行检测，这个检测是并发的完成的
 * 所以需要控制如果一个表所有的情况检测完成，再进行后续的操作
 *
 *解决
 * 利用 `CountDownLatch`的计数器
 * 每当一个检测完成，计数器减一
 * 如果计数为0，执行后面操作
 * @author shen_wzhong
 * @create 2022-04-21 17:27
 */
public class CountDownLatchTest {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {
        //2个事件请求，这里只演示校验数据行，和数据schema
        Event[] events = {new Event(1), new Event(2)};

        ExecutorService service = Executors.newFixedThreadPool(5);

        for (Event event : events) {
            //2个事件请求中，可能涉及多个表，可以再分成多个表
            List<Table> tables = capture(event);
            for (Table table : tables) {
                TaskBatch taskBatch = new TaskBatch(2);
                TrustSourceColumns sourceColumns = new TrustSourceColumns(table, taskBatch);
                TrustSourceRecordCount recordCount = new TrustSourceRecordCount(table, taskBatch);
                service.submit(sourceColumns);
                service.submit(recordCount);
            }
        }
    }

    static class Event {
        private int id;

        Event(int id) {
            this.id = id;
        }
    }

    interface Watcher {
        void done(Table table);
    }

    static class TaskBatch implements Watcher {

        private final CountDownLatch latch;

        TaskBatch(int size) {
            this.latch = new CountDownLatch(size);
        }

        @Override
        public void done(Table table) {
            latch.countDown();
            if (latch.getCount() == 0) {
                System.out.println("The table " + table.tableName + " finished work , " + table.toString());
            }
        }
    }

    static class Table {
        String tableName;
        long sourceRecordCount;
        long targetCount;
        String columnSchema = "columnXXXType = varchar";

        String targetColumnSchema = "";

        public Table(String tableName, long sourceRecordCount) {
            this.tableName = tableName;
            this.sourceRecordCount = sourceRecordCount;

        }

        @Override
        public String toString() {
            return "Table{" +
                    "tableName='" + tableName + '\'' +
                    ", sourceRecordCount=" + sourceRecordCount +
                    ", targetCount=" + targetCount +
                    ", columnSchema='" + columnSchema + '\'' +
                    ", targetColumnSchema='" + targetColumnSchema + '\'' +
                    '}';
        }
    }

    private static List<Table> capture(Event event) {
        List<Table> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new Table("table-" + event.id + "-" + i, i * 1000));
        }
        return list;
    }

    //校验数据行数是否一致
    static class TrustSourceRecordCount implements Runnable {

        private final Table table;

        private final TaskBatch taskBatch;

        TrustSourceRecordCount(Table table, TaskBatch taskBatch) {
            this.table = table;
            this.taskBatch = taskBatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(RANDOM.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            table.targetCount = table.sourceRecordCount;

            taskBatch.done(table);

        }

    }
    //校验数据列属性以及对应的表
    static class TrustSourceColumns implements Runnable {

        private final Table table;

        private final TaskBatch taskBatch;

        TrustSourceColumns(Table table, TaskBatch taskBatch) {
            this.table = table;
            this.taskBatch = taskBatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(RANDOM.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            table.targetColumnSchema = table.columnSchema;

            taskBatch.done(table);
        }

    }
}
