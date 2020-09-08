package i.dtream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static i.dtream.Goods.goodNum;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class Order {
    public CustomLock lock;
    private Object sync = new Object();
    private ExecutorService executorService = Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public void doOrder() {
        lock = new CustomLock(5);
        int count = 0;
        Goods.init(lock);

        List<Goods.GoodEntry> toBeConsumed = Goods.goods.subList(0, 5);
        final int threadNum = 20;
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    StringBuffer msg = new StringBuffer();
                    lock.lock();
                    goodNum--;
                    try {
                        msg.append("i am consumer:" + finalI);
                        Goods.GoodEntry entry = toBeConsumed.size() > 0 ? toBeConsumed.remove(0) : null;
                        if (null == entry) {
                            msg.append("\r\nI am hungry");
                        } else {
                            msg.append("\r\nthe good entry:" + (entry == null ? "" : entry.toString()));
                        }

                        if (finalI == threadNum - 1) {
                            lock.getCondition().signal();
                        }
                    } catch (Exception e) {
                        System.out.println("" + e.getMessage());
                    } finally {
                        lock.unlock();
                    }

                    System.out.println("--- " + msg.toString());

                }
            });


        }

        lock.lock();
        try {
            lock.getCondition().await();
        } catch (InterruptedException e) {
            System.out.println("" + e.getStackTrace());

        } finally {
            lock.unlock();

        }

        System.out.println("remain count:" + goodNum);

    }
}
