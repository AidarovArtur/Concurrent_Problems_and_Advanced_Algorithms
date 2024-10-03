/*
 * This file implements a thread-safe mutable integer array, ensuring safe concurrent access and modification by multiple threads. Key concepts: thread-safety, synchronization.
 */

public class ThreadSafeMutableIntArray {
    private static int[] arr;
    private static Object[] locks;
    public ThreadSafeMutableIntArray(int n){
        arr = new int[n];
        locks = new Object[n];
        int i=0;
        while(i<n){
            locks[i] = new Object();
            i++;
        }
    }
    public void set(int idx, int newValue){
        synchronized(locks[idx]){
            if(idx < arr.length){
                arr[idx] = newValue;
            }else{ throw new IndexOutOfBoundsException();}
        }
    }

    public int get(int idx){
        synchronized(locks[idx]){
            if(idx < arr.length){
                return (int) arr[idx];
            }else{
                throw new IndexOutOfBoundsException();
            }
        }
    }
    public synchronized int incremenT(int idx){
        return arr[idx]=arr[idx]+1;
    }
    public synchronized int dincremenT(int idx){
        return arr[idx]=arr[idx]-1;
    }
    static class ArrIncr implements Runnable{
        ThreadSafeMutableIntArray arr;
        public ArrIncr(ThreadSafeMutableIntArray arr){this.arr = arr;}
        @Override
        public void run() {
            for (int i = 0; i < 10_000_000; i++) {
                // arr.set(arr.get(0)+1);
                arr.incremenT(0);
            }
        }
    }
    static class ArrDec implements Runnable{
        ThreadSafeMutableIntArray arr;
        public ArrDec(ThreadSafeMutableIntArray arr){this.arr = arr;}
        @Override
        public void run() {
            for (int i = 0; i < 10_000_000; i++) {
                // arr.set(arr.get(0)-1);
                arr.dincremenT(0);
            }
        }
    }
    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        ThreadSafeMutableIntArray safeArr = new ThreadSafeMutableIntArray(1);
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new ArrIncr(safeArr));
        }
        for (int i = 5; i < threads.length; i++) {
            threads[i] = new Thread(new ArrDec(safeArr));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception e) {
        }
        System.out.println("If we increment "+safeArr.get(0));
        //System.out.println("If we decrement "+safeArr.get(1));
    }
}
// public class MsLunch {

//     private long c1 = 0;
//     private long c2 = 0;

//     private Object lock1 = new Object();
//     private Object lock2 = new Object();

//     public void inc1() {
//         synchronized(lock1) {
//             c1++;
//         }
//     }

//     public void inc2() {
//         synchronized(lock2) {
//             c2++;
//         }
//     }
// }