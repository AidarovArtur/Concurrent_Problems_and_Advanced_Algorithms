/*
 * This file implements a thread-safe mutable integer to ensure safe concurrent access and modification. Key concepts: thread-safety, synchronization.
 */

import java.util.function.IntUnaryOperator;

public class ThreadSafeMutableInteger
{
    static class Incrementer implements Runnable
    {
        ThreadSafeMutableInteger atomicInt;
        public Incrementer(ThreadSafeMutableInteger atomicInt) { this.atomicInt = atomicInt; }
        public void run() {
            for (int i = 0; i < 10_000_000; i++)
                atomicInt.set(atomicInt.get()+1);
        }
    }
    static class IncDec implements Runnable
    {
        ThreadSafeMutableInteger atomicInt;
        boolean isInc;
        public IncDec(ThreadSafeMutableInteger atomicInt, boolean isInc) {
            this.atomicInt = atomicInt;
            this.isInc = isInc;
        }
        public void run() {
            for (int i = 0; i < 10_000_000; i++)
                if (isInc) atomicInt.getAndIncrement();
                else atomicInt.getAndDecrement();
        }
    }
    static class IncDecByTwo implements Runnable
    {
        ThreadSafeMutableInteger atomicInt;
        boolean isInc;
        public IncDecByTwo(ThreadSafeMutableInteger atomicInt, boolean isInc) {
            this.atomicInt = atomicInt;
            this.isInc = isInc;
        }
        public void run() {
            for (int i = 0; i < 10_000_000; i++)
                if (isInc) atomicInt.addAndGet(2);
                else atomicInt.addAndGet(-2);
        }
    }
    static class UnaryOp implements Runnable
    {
        ThreadSafeMutableInteger atomicInt;
        boolean isInc;
        public UnaryOp(ThreadSafeMutableInteger atomicInt, boolean isInc) {
            this.atomicInt = atomicInt;
            this.isInc = isInc;
        }
        public void run() {
            for (int i = 0; i < 10_000_000; i++)
                if (isInc) atomicInt.getAndUpdate(x -> x+3);
                else atomicInt.getAndUpdate(x -> x-2);
        }
    }
    private int atomicInteger;
    public ThreadSafeMutableInteger() {}
    public ThreadSafeMutableInteger(int atomicInteger) {
        this.atomicInteger = atomicInteger;
    }
    public int get() { return atomicInteger; }
    public void set(int atomicInteger) { this.atomicInteger = atomicInteger; }
    public synchronized int getAndIncrement() {
        return atomicInteger++;
    }
    public synchronized int getAndDecrement() {
        return atomicInteger--;
    }
    public synchronized int getAndAdd(int v) {
        int oldValue = v;
        atomicInteger += v; 
        return oldValue;
    }
    public synchronized int incrementAndGet() {
        return ++atomicInteger;
    }
    public synchronized int decrementAndGet() {
        return --atomicInteger;
    }
    public synchronized int addAndGet(int v) {
        return atomicInteger += v;
    }
    public synchronized int getAndUpdate(IntUnaryOperator iuo) {
        int oldValue = atomicInteger;
        atomicInteger = iuo.applyAsInt(atomicInteger);
        return oldValue;
    }
    public synchronized int updateAndGet(IntUnaryOperator iuo) {
        return atomicInteger = iuo.applyAsInt(atomicInteger);
    }
    public static void main(String[] args) {
        ThreadSafeMutableInteger tsmi = new ThreadSafeMutableInteger();
        Thread[] threads = new Thread[10];
        // for (int i = 0; i < threads.length; i++) {
        //     threads[i] = new Thread(new Incrementer(tsmi));
        // }
        // for (int i = 0; i < threads.length; i++)
        //     threads[i].start();
        // try {
        //     for (int i = 0; i < threads.length; i++)
        //         threads[i].join();
        // } catch (InterruptedException e) {}
        // System.out.println(tsmi.get());
        
        // tsmi.set(0);
        // for (int i = 0; i < threads.length; i++)
        //     threads[i] = new Thread(new IncDec(tsmi, (i % 2) != 0));
        // for (int i = 0; i < threads.length; i++)
        //     threads[i].start();
        // try {
        //     for (int i = 0; i < threads.length; i++)
        //         threads[i].join();
        // } catch (InterruptedException e) {}
        // System.out.println(tsmi.get());

        // for (int i = 0; i < threads.length; i++)
        //     threads[i] = new Thread(new IncDecByTwo(tsmi, (i % 2) != 0));
        // for (int i = 0; i < threads.length; i++)
        //     threads[i].start();
        // try {
        //     for (int i = 0; i < threads.length; i++)
        //         threads[i].join();
        // } catch (InterruptedException e) {}
        // System.out.println(tsmi.get());
        tsmi.set(0);
        for (int i = 0; i < threads.length; i++)
            threads[i] = new Thread(new UnaryOp(tsmi, (i % 2) != 0));
        for (int i = 0; i < threads.length; i++)
            threads[i].start();
        try {
            for (int i = 0; i < threads.length; i++)
                threads[i].join();
        } catch (InterruptedException e) {}
        System.out.println(tsmi.get());
    }
}