package i.dtream;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class CustomLock implements Lock {
    private FairSync sync;
    private Condition condition;

    class FairSync extends AbstractQueuedSynchronizer {
        public FairSync init(int capacity) {
            setState(capacity);
            return this;
        }

        @Override
        protected boolean tryAcquire(int arg) {
            for (;;) {
                int state = getState();
                if (state > 0 && !hasQueuedPredecessors()) {
                    return compareAndSetState(state, state - 1);
                } else {
                    return false;
                }
            }
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }


    public Condition getCondition() {
        return condition;
    }

    public CustomLock(int capacity) {
        sync = new FairSync().init(capacity);
        condition = newCondition();
    }

    @Override
    public void lock() {
        sync.acquire(1);

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        sync.tryAcquire(1);

        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.release(1);

    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
