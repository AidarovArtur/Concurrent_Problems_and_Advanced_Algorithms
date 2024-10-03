/*
 * This file demonstrates an inefficient sorting algorithm, showcasing the basic sorting operations and opportunities for optimization. Key concepts: algorithm design, sorting.
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class InefficientSorter {
    public static void main(String[] args) {
        final int n = 10;
        ExecutorService es = Executors.newFixedThreadPool(n);
        int[] ar = new int[100];
        Random rand = new Random();

        for (int i = 0; i < 100; i++) {
            // Obtain a number between [0 - 100].
            int r = rand.nextInt(101);
            ar[i] = r;
        }
        StringBuilder sb = new StringBuilder();
        int sumb = 0;
        sb.append("[");
        for (int i : ar) {
            sumb += i;
            sb.append(" "+i+" ");
        }
        sb.append("]");
        System.out.println(sb);
        System.out.println("Sum before: "+sumb);
        for (int i = 0; i < n; i++) {
            es.submit(() -> {
                for(int j = 0; j < 100000; j++){
                    int idx1 = ThreadLocalRandom.current().nextInt(0, 100);
                    int idx2 = ThreadLocalRandom.current().nextInt(0, 100);
                    if (idx1 < idx2 && ar[idx1] > ar[idx2]) {
                        int temp = ar[idx1];
                        ar[idx1] = ar[idx2];
                        ar[idx2] = temp;
                    }
                }
            });
        }
        int suma=0;
        StringBuilder sb1 = new StringBuilder();
        sb1.append("[");
        for (int i : ar) {
            suma+=i;
            sb1.append(" "+i+" ");
        }
        sb1.append("]");
        System.out.println("Sum before: "+suma);
        System.out.println(sb1);
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {}
    }
}
