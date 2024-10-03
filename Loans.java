/*
 * This file implements a system to manage loans, showcasing basic financial operations and object-oriented design principles. Key concepts: object-oriented programming, financial system design.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Loans
{
    static class Bank
    {
        int totalLoans;
        public synchronized int takeLoan(int loanSize) {
            totalLoans += loanSize;
            return loanSize;
        }
        public int getTotalLoans() { return totalLoans; }
    }
    public static void main(String[] args) {
        final int n = 10;
        ExecutorService es = Executors.newFixedThreadPool(n);
        Bank b = new Bank();
        int[] loansTaken = new int[n];
        for (int i = 0; i < n; i++) {
            final int finali = i;
            es.submit(() -> {
                for (int j = 0; j < 10000; j++) {
                    int loanAmount = ThreadLocalRandom.current().nextInt(1000, 10000);
                    loansTaken[finali] += b.takeLoan(loanAmount);
                }
            });
        }
        //Average expected value: (1000*10000*10+9999*10000*10)/2==549950000.0
        List<Future<Integer>> futures = new ArrayList<>();
        Bank bother = new Bank();
        for (int i = 0; i < n; i++) {
            futures.add(es.submit(() -> {
                int loansTotal = 0;
                for (int j = 0; j < 10000; j++) {
                    int loanAmount = ThreadLocalRandom.current().nextInt(1000, 10000);
                    loansTotal += bother.takeLoan(loanAmount);
                }
                return loansTotal;
            }));
        }
        int totalLoansTaken = futures.stream().mapToInt(x -> {
            try { return x.get(); }
            catch (InterruptedException|ExecutionException e) { return 0; }
        }).sum();
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {}
        System.out.println(b.getTotalLoans());
        System.out.println(Arrays.stream(loansTaken).sum());
        System.out.println(bother.getTotalLoans());
        System.out.println(totalLoansTaken);
    }
}