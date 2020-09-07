package i.dtream;

import java.util.concurrent.locks.Condition;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class OrderTest {
    public static CustomLock lock;
    public static void main(String args[]) {

        lock = new CustomLock(5);
        Goods.init(lock);

        try {
            lock.getCondition().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 100; i++) {
            lock.lock();
        }


    }
}
