package fr.iamacat.optimizationsandtweaks.utilsformods.industrialcraft2;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterators;

import ic2.core.util.PriorityExecutor;

public class FixedPriorityQueue2<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final Map<PriorityExecutor.Priority, Queue<E>> queues = new EnumMap<>(PriorityExecutor.Priority.class);

    public FixedPriorityQueue2() {
        PriorityExecutor.Priority[] var1 = PriorityExecutor.Priority.values();
        for (PriorityExecutor.Priority priority : var1) {
            this.queues.put(priority, new ArrayDeque<>());
        }
    }

    public E poll() {
        Iterator<Queue<E>> var1 = this.queues.values()
            .iterator();

        E ret;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            Queue<E> queue = var1.next();
            ret = queue.poll();
        } while (ret == null);

        return ret;
    }

    public E peek() {
        Iterator<Queue<E>> var1 = this.queues.values()
            .iterator();

        Object ret;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            Queue<E> queue = var1.next();
            ret = queue.peek();
        } while (ret == null);

        return (E) ret;
    }

    public int size() {
        int ret = 0;

        Queue<E> queue;
        for (Iterator<Queue<E>> var2 = this.queues.values()
            .iterator(); var2.hasNext(); ret += queue.size()) {
            queue = var2.next();
        }

        return ret;
    }

    public Iterator<E> iterator() {
        List<Iterator<E>> iterators = new ArrayList<>(this.queues.size());

        for (Queue<E> es : this.queues.values()) {
            iterators.add(es.iterator());
        }

        return Iterators.concat(iterators.iterator());
    }

    public boolean offer(E e) {
        ArrayDeque<E> queue = (ArrayDeque<E>) queues.get(getPriority(e));
        queue.offer(e);
        return true;
    }

    public void put(E e) throws InterruptedException {
        this.offer(e);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) {
        return this.offer(e);
    }

    public synchronized E take() throws InterruptedException {
        E ret;
        for (ret = this.poll(); ret == null; ret = this.poll()) {
            this.wait();
        }

        return ret;
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

                this.wait(duration / 1000000L, (int) (duration % 1000000L));
                ret = this.poll();
            } while (ret == null);

            return ret;
        }
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public int drainTo(Collection<? super E> c) {
        return this.drainTo(c, Integer.MAX_VALUE);
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        int ret = 0;

        for (Queue<E> es : this.queues.values()) {
            for (; ret < maxElements; ++ret) {
                E x = es.poll();
                if (x == null) {
                    break;
                }

                c.add(x);
            }
        }

        return ret;
    }

    public void clear() {

        for (Queue<E> es : this.queues.values()) {
            es.clear();
        }

    }

    public boolean contains(Object o) {
        Iterator<Queue<E>> var2 = this.queues.values()
            .iterator();

        Queue<E> queue;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            queue = var2.next();
        } while (!queue.contains(o));

        return true;
    }

    public boolean removeAll(Collection<?> c) {
        boolean ret = false;

        for (Queue<E> es : this.queues.values()) {
            if (es.removeAll(c)) {
                ret = true;
            }
        }

        return ret;
    }

    public boolean retainAll(Collection<?> c) {
        boolean ret = false;

        for (Queue<E> es : this.queues.values()) {
            if (es.retainAll(c)) {
                ret = true;
            }
        }

        return ret;
    }

    public Object[] toArray() {
        return super.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    public String toString() {
        return super.toString();
    }

    public boolean addAll(Collection<? extends E> c) {
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
        return x instanceof PriorityExecutor.CustomPriority ? ((PriorityExecutor.CustomPriority) x).getPriority()
            : PriorityExecutor.Priority.Default;
    }
}
