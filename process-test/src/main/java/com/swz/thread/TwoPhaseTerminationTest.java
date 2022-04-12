package com.swz.thread;

import lombok.extern.slf4j.Slf4j;

/**设计模式
 *      两阶段终止模式
 * @author shen_wzhong
 * @create 2022-04-11 14:38
 */
@Slf4j(topic = "c.TwoPhaseTerminationTest")
public class TwoPhaseTerminationTest {
    public static void main(String[] args) {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();

        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tpt.stop();
    }

}

@Slf4j(topic = "c.TwoPhaseTermination")
class TwoPhaseTermination {
    private Thread thread;

    //启动监控线程
    public void start(){
        thread = new Thread(() -> {
            while(true) {
                Thread current = Thread.currentThread();
                if(current.isInterrupted()) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);
                    log.debug("执行监控操作");
                } catch (InterruptedException e) {
                    //如果在sleep中打断，会进入异常，而且Interrupted标记是false
                    //这里重新设置打断标记
                    current.interrupt();
                }
            }
        },"监控线程");
        thread.start();
    }

    //停止监控线程
    public void stop() {
        thread.interrupt();
    }
}
