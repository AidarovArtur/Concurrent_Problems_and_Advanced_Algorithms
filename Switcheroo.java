/*
 * This file implements a system where threads swap resources in a synchronized manner. Key concepts: resource swapping, synchronization.
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Switcheroo {

    public static void main(String[] args) {
        final int n = 10;
        ExecutorService es = Executors.newFixedThreadPool(n);
        int[] ar = new int[100];
        int sumb=0;
        for(int i=0; i<100; i++){
            ar[i] = 1000;
            sumb += 1000;
        }
        System.out.println("Sum before: "+sumb);
        for(int i=0; i<n; i++){
            es.submit(() -> {
                int amount = ThreadLocalRandom.current().nextInt(1, 1000);
                int idx1 = ThreadLocalRandom.current().nextInt(0, 100);
                int idx2 = ThreadLocalRandom.current().nextInt(0, 100);
                ar[idx1] += amount;
                ar[idx2] -= amount;
            });
        }
        int suma=0;
        for (int i : ar) {
            suma+=i;
        }
        System.out.println("Sum after: "+suma);
        es.shutdown();
    }
}