package i.dtream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class Goods {
    private volatile static int goodNum = 100;

    public static List<GoodEntry> goods = new ArrayList<>(100);

    static class GoodEntry {

        public GoodEntry(int id, String name, String desc) {
            this.id = id;
            this.name = name;
            this.desc = desc;
        }

        private int id;
        private String name;
        private String desc;

    }

    public static void init(CustomLock lock) {

        for (int i =0; i < goodNum; i++) {
            GoodEntry entry = new GoodEntry(i, "apple" + i, "");
            goods.add(entry);
        }

        lock.getCondition().signal();

    }
}
