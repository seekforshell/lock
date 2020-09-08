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
    private volatile long epoch = 0;

    class FairSync extends AbstractQueuedSynchronizer {
        private final int capacity;

        FairSync(int capacity) {
            this.capacity = capacity;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        public FairSync init(int capacity) {
            epoch = 1;
            setState(capacity);
            return this;
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(capacity);

            return true;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            for (;;) {
                int current = getState();
                int next = current + arg;
                if (next > capacity) // overflow
                    throw new Error("Maximum permit count exceeded");
                if (compareAndSetState(current, next))
                    return true;
            }
        }

        @Override
        protected int tryAcquireShared(int arg) {
            for (;;) {
                int state = getState();
                if (hasQueuedPredecessors() || state < 1) {
                    return -1;
                }

                int remain = state - arg;
                if (compareAndSetState(state, remain)) {
                    return remain;
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
        sync = new FairSync(capacity).init(capacity);
        condition = newCondition();
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        sync.acquire(1);

        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);

    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
