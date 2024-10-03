/*
 * This file implements a stock market simulation where multiple buyers and sellers interact. It demonstrates concurrency and synchronization in market transactions. Key concepts: concurrency, synchronization.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
// import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class stock {
    public static void main(String[] args) {
        final int n = 100;
        Random rand = new Random();
        ExecutorService es = Executors.newFixedThreadPool(n);

        Map<String, Double> map = Collections.synchronizedMap(new HashMap<>());
        //Map<String, Double> map = new ConcurrentHashMap<>();
        map.put("STR", (double) 100);
        map.put("APL", (double) 100);
        map.put("SMS", (double) 100);
        long startTime = System.nanoTime();
        int i;
        for (i = 0; i < n; i++) {
            es.submit(() -> {
                for (int j = 0; j < 10000; j++) {
                    synchronized (map) {
                        int var = rand.nextInt(1, 3);
                        if (var == 1) {
                            // kindof buying
                            int stockNum = rand.nextInt(0, 3);
                            if (stockNum == 0) {
                                map.put("STR", map.get("STR") + 1);
                            } else if (stockNum == 1) {
                                map.put("APL", map.get("APL") + 1);
                            } else {
                                map.put("SMS", map.get("SMS") + 1);
                            }
                        } else if (var == 2) {
                            // kindof selling
                            int stockNum = rand.nextInt(0, 3);
                            if (stockNum == 0) {
                                map.put("STR", map.get("STR") - 1);
                            } else if (stockNum == 1) {
                                map.put("APL", map.get("APL") - 1);
                            } else {
                                map.put("SMS", map.get("SMS") - 1);
                            }
                        }
                    }
                }
            });
        }

        Thread t = new Thread(() -> {
            while (Thread.activeCount()  > 2) {
                synchronized (map) {
                    System.out.println("STR value: " + map.get("STR"));
                    System.out.println("APL value: " + map.get("APL"));
                    System.out.println("SMS value: " + map.get("SMS"));
                }
            }
        });
        t.start();
        try {
            es.shutdown();
            while (!es.awaitTermination(120, TimeUnit.SECONDS)) {
            }
            t.join();

            long endTime = (((System.nanoTime() - startTime)) / 1000000);
            System.out.println("Completed in : " + endTime + " milliSeconds");
        } catch (InterruptedException e) {
        }
    }

}
