package fr.iamacat.multithreading.utils.multithreadingandtweaks.industrialcraft2;

import com.google.common.collect.Iterators;
import ic2.core.util.PriorityExecutor;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class FixedPriorityQueue2<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    private final Map<PriorityExecutor.Priority, Queue<E>> queues = new EnumMap(PriorityExecutor.Priority.class);

    public FixedPriorityQueue2() {
        PriorityExecutor.Priority[] var1 = PriorityExecutor.Priority.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            PriorityExecutor.Priority priority = var1[var3];
            this.queues.put(priority, new ConcurrentLinkedQueue<>());
        }

    }

    public synchronized E poll() {
        Iterator var1 = this.queues.values().iterator();

        Object ret;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            Queue<E> queue = (Queue)var1.next();
            ret = queue.poll();
        } while(ret == null);

        return (E) ret;
    }

    public synchronized E peek() {
        Iterator var1 = this.queues.values().iterator();

        Object ret;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            Queue<E> queue = (Queue)var1.next();
            ret = queue.peek();
        } while(ret == null);

        return (E) ret;
    }

    public synchronized int size() {
        int ret = 0;

        Queue queue;
        for(Iterator var2 = this.queues.values().iterator(); var2.hasNext(); ret += queue.size()) {
            queue = (Queue)var2.next();
        }

        return ret;
    }

    public synchronized Iterator<E> iterator() {
        List<Iterator<E>> iterators = new ArrayList(this.queues.size());
        Iterator var2 = this.queues.values().iterator();

        while(var2.hasNext()) {
            Queue<E> queue = (Queue)var2.next();
            iterators.add(queue.iterator());
        }

        return Iterators.concat(iterators.iterator());
    }

    public synchronized boolean offer(E e) {
        Queue<E> queue = (Queue)this.queues.get(this.getPriority(e));
        queue.offer(e);
        this.notify();
        return true;
    }

    public void put(E e) throws InterruptedException {
        this.offer(e);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.offer(e);
    }

    public synchronized E take() throws InterruptedException {
        Object ret;
        for(ret = this.poll(); ret == null; ret = this.poll()) {
            this.wait();
        }

        return (E) ret;
    }

    public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E ret = this.poll();
        if (ret != null) {
            return ret;
        } else {
            long endTime = System.nanoTime() + unit.toNanos(timeout);

            do {
                long duration = endTime - System.nanoTime();
                if (duration <= 0L) {
                    break;
                }

                this.wait(duration / 1000000L, (int)(duration % 1000000L));
                ret = this.poll();
            } while(ret == null);

            return ret;
        }
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public int drainTo(Collection<? super E> c) {
        return this.drainTo(c, Integer.MAX_VALUE);
    }

    public synchronized int drainTo(Collection<? super E> c, int maxElements) {
        int ret = 0;
        Iterator var4 = this.queues.values().iterator();

        while(var4.hasNext()) {
            for(Queue<E> queue = (Queue)var4.next(); ret < maxElements; ++ret) {
                E x = queue.poll();
                if (x == null) {
                    break;
                }

                c.add(x);
            }
        }

        return ret;
    }

    public synchronized void clear() {
        Iterator var1 = this.queues.values().iterator();

        while(var1.hasNext()) {
            Queue<E> queue = (Queue)var1.next();
            queue.clear();
        }

    }

    public synchronized boolean contains(Object o) {
        Iterator var2 = this.queues.values().iterator();

        Queue queue;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            queue = (Queue)var2.next();
        } while(!queue.contains(o));

        return true;
    }

    public synchronized boolean removeAll(Collection<?> c) {
        boolean ret = false;
        Iterator var3 = this.queues.values().iterator();

        while(var3.hasNext()) {
            Queue<E> queue = (Queue)var3.next();
            if (queue.removeAll(c)) {
                ret = true;
            }
        }

        return ret;
    }

    public synchronized boolean retainAll(Collection<?> c) {
        boolean ret = false;
        Iterator var3 = this.queues.values().iterator();

        while(var3.hasNext()) {
            Queue<E> queue = (Queue)var3.next();
            if (queue.retainAll(c)) {
                ret = true;
            }
        }

        return ret;
    }

    public synchronized Object[] toArray() {
        return super.toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    public synchronized String toString() {
        return super.toString();
    }

    public synchronized boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        boolean added = false;
        for (E e : c) {
            Queue<E> queue = this.queues.get(this.getPriority(e));
            queue.offer(e);
            added = true;
        }
        if (added) {
            this.notifyAll();
        }
        return added;
    }

    private PriorityExecutor.Priority getPriority(E x) {
        return x instanceof PriorityExecutor.CustomPriority ? ((PriorityExecutor.CustomPriority)x).getPriority() : PriorityExecutor.Priority.Default;
    }
}

