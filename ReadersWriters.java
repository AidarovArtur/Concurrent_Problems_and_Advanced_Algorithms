/*
 * This file implements the Readers-Writers problem, which manages concurrent access to a shared resource by multiple readers and writers using synchronization techniques. Key concepts: concurrency, synchronization, resource management.
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadersWriters {
    private static final int NUMBER_OF_PROCESSES = 100;
    private static final int DATABASE_SIZE = 100;
    private static final int WRITER_RATIO = 5;
    private static final int MAX_VALUE = 1000;
    private static final int DELAY = 100;

    private static int[] dataBase = new int[DATABASE_SIZE];
    private static ReadWriteLock rwl = new ReentrantReadWriteLock(true);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 0; i < NUMBER_OF_PROCESSES; ++i) {
            pool.submit(() -> {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                int index = rnd.nextInt(DATABASE_SIZE);

                if (rnd.nextInt(WRITER_RATIO) == 0) {
                    int value = rnd.nextInt(MAX_VALUE);
                    sleep(DELAY);
                    //synchronized(dataBase)
                        rwl.writeLock().lock();
                        dataBase[index] = value;
                        rwl.writeLock().unlock();
                    //}
                    System.err.println("Written value " + value + " to index " + index);
                } else {
                    int value;
                    sleep(DELAY);
                    //synchronized(dataBase){
                        rwl.readLock().lock();
                        value = dataBase[index];
                        rwl.readLock().unlock();
                    //}
                    System.err.println("Read value " + value + " from index " + index);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }

    static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
