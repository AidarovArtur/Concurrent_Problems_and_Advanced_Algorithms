/*
 * This file compares the performance of different map data structures (HashMap, TreeMap) for insertion and retrieval operations. Key concepts: data structures, performance comparison.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class MapSpeed
{
    public static void test(int insertThread, int deleteThread, int iterateThread,
        int numOp, boolean isWrapper)
    {
        ArrayList<Thread> threads = new ArrayList<>();
        Map<Integer, Integer> map = isWrapper
            ? Collections.synchronizedMap(new HashMap<>()) : new ConcurrentHashMap<>();
        for (int i = 0; i < insertThread; i++) {
            threads.add(new Thread(() -> {
                int key = ThreadLocalRandom.current().nextInt(0, map.size());
                int value = ThreadLocalRandom.current().nextInt(0, map.size());
                map.put(key, value);
            }));
        }
        // for (int i = 0; i < deleteThread; i++) {
        //     threads.add(new Thread(() -> {
        //         synchronized (map) {
        //             ThreadLocalRandom.current().nextLong(0, map.size());
        //             map.keySet().stream().drop
        //             map.remove(key);
        //         }
        //     }));
        // }
        for (int i = 0; i < iterateThread; i++) {
            threads.add(new Thread(() -> {
                for (Integer x : map.keySet()) { System.out.println(x + ":" + map.get(x)); }
            }));
        }
        for (int i = 0; i < threads.size(); i++) threads.get(i).start();
        try {
            for (int i = 0; i < threads.size(); i++) threads.get(i).join();
        } catch (InterruptedException e) {}
    }
    public static void main(String[] args)
    {

    }
}
