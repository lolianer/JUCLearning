package com.swz.chapter06;

import com.swz.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**但是有时候，并**不关心引用变量更改了几次**，只是单纯的关心**是否更改过**，所以就有了 **AtomicMarkableReference**
 * @author shen_wzhong
 * @create 2022-04-15 15:53
 */
@Slf4j(topic = "c.AtomicMarkableReferenceTest")
public class AtomicMarkableReferenceTest {
    public static void main(String[] args) {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        //参数2mark可以看作一个标记，表示垃圾袋满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, true);

        log.debug("start...");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());

        new Thread(() -> {
            log.debug("start");
            ref.compareAndSet(bag, new GarbageBag("空垃圾袋"), true, false);
            log.debug(ref.getReference().toString());
        },"保洁阿姨").start();

        Sleeper.sleep(1);
        log.debug("想换一只新垃圾袋");
        boolean success = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了吗？" + success);
        log.debug(ref.getReference().toString());
    }


}

class GarbageBag {
    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
}
