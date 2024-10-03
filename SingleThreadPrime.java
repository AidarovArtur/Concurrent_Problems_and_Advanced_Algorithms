/*
 * This file implements a single-threaded prime number finder for comparison with a multi-threaded version. Key concepts: single-threaded processing, algorithm comparison.
 */

import java.util.ArrayList;
import java.util.List;

public class SingleThreadPrime {

    public static List<Integer> primeFinder(int n) {
        long start = System.currentTimeMillis();
        List<Integer> primes = new ArrayList<>();
        boolean[] nums = new boolean[n+1];
        for (int i=2; i<=n; i++) {
            if (nums[i])
                continue;
            primes.add(i);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
            for (int j=i; j<=n; j+=i)
                nums[j] = true;
        }
        long end = System.currentTimeMillis();
        System.out.format("Found %d primes in %d ms\n", primes.size(), end - start);
        return primes;
    }

} 
