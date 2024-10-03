/*
 * This file implements a custom map data structure using hashing and collision resolution. Key concepts: data structures, hashing, collision resolution.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class mymap {

    public static void main(String[] args) {
        Map<Integer, Integer> mp = Collections.synchronizedMap(new HashMap<>());
        long startTime = System.nanoTime();

        Thread[] threads = new Thread[10];
        // Insertion with synchronized Map
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                synchronized (mp) {
                    for (int j = 0; j < 1000; j++) {
                        int key = ThreadLocalRandom.current().nextInt(0, 1000);
                        int value = ThreadLocalRandom.current().nextInt(0, 1000);
                        mp.put(key, value);
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        // Deletion
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                synchronized (mp) {
                    for (int j = 0; j < 1000; j++) {
                        int key1 = ThreadLocalRandom.current().nextInt(0, 1000);
                        if (mp.containsKey(key1)) {
                            mp.remove(key1);
                        }
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        // Iteration
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                synchronized (mp) {
                    // for (Map.Entry<Integer, Integer> entry : mp.entrySet()) {
                    //     String s = ("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    // }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        long endTime = (((System.nanoTime() - startTime)) / 1000000);
        System.out.println("Hand made syncronization Completed in: " + endTime + " milliSeconds");

        
        Thread[] ts = new Thread[10];
        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        long startTime1 = System.nanoTime();

        // Insertion with synchronized Map
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    int key = ThreadLocalRandom.current().nextInt(0, 1000);
                    int value = ThreadLocalRandom.current().nextInt(0, 1000);
                    map.put(key, value);
                }
            });
        }
        for (Thread thread : ts) {
            thread.start();
        }
        for (Thread thread : ts) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        // Deletion
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    int key1 = ThreadLocalRandom.current().nextInt(0, 1000);
                    if (map.containsKey(key1)) {
                        map.remove(key1);
                    }
                }
            });
        }
        for (Thread thread : ts) {
            thread.start();
        }
        for (Thread thread : ts) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        // Iteration
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(() -> {
                // for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                //     String s = ("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                // }
            });
        }
        for (Thread thread : ts) {
            thread.start();
        }
        for (Thread thread : ts) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        long endTime1 = (((System.nanoTime() - startTime1)) / 1000000);
        System.out.println("With biult in HashMap time: " + endTime1 + " millis");
    }
}
