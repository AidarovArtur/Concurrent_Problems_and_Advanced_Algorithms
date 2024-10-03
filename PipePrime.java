/*
 * This file implements a multi-threaded prime number finder using pipeline processing to parallelize the task. Key concepts: multi-threading, parallel processing.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class PipePrime {

    private static final ConcurrentSkipListSet<Integer> primes = new ConcurrentSkipListSet<>();

    // private static final Lock lock = new ReentrantLock(); // alternatively
    // private static final Condition finished = lock.newCondition();
    // private static boolean lastDone = false;

    private static final SynchronousQueue<Object> allDone = new SynchronousQueue<>();

    static class Node implements Runnable {

        private final DataInputStream in;

        public Node(InputStream in) {
            this.in = new DataInputStream(in);
        }

        @Override
        public void run() {

            int first = -1;
            int next = -1;
            try {

                try {
                    first = in.readInt();
                    // System.out.println("Next prime: " + first);
                    primes.add(first);
                } catch (EOFException e) {
                    try {
                        allDone.put(new Object());
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                PipedInputStream nextIn = new PipedInputStream();
                try (DataOutputStream nextOut = new DataOutputStream(new PipedOutputStream(nextIn));
                        var prevIn = in) {

                    new Thread(new Node(nextIn)).start();

                    while (true) {

                        next = prevIn.readInt();

                        if (next % first == 0) {
                            continue;
                        }
                        nextOut.writeInt(next);
                        nextOut.flush();
                    }
                } catch (EOFException e) {
                    System.out.printf("Last Thread %s finished.\n", Thread.currentThread().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    // if (first == -1 && next == -1) {

                    // // try {
                    // // lock.lock();
                    // // System.out.println("all done");
                    // // lastDone = true;
                    // // finished.signalAll();
                    // // } finally {
                    // // lock.unlock();
                    // // }

                    // try {
                    // allDone.put(new Object());
                    // } catch (InterruptedException interruptedException) {
                    // interruptedException.printStackTrace();
                    // }
                }
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static List<Integer> doIt(int n) {
        PipedInputStream in = new PipedInputStream();
        try (DataOutputStream out = new DataOutputStream(new PipedOutputStream(in))) {
            new Thread(new Node(in)).start();

            for (int i = 2; i <= n; i++) {
                out.writeInt(i);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } // close out automatically

        // try {
        // lock.lock();
        // while (!lastDone) {
        // finished.await(10000, TimeUnit.MILLISECONDS);
        // lock.lock();
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // } finally {
        // lock.unlock();
        // }

        try {
            allDone.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(primes);
    }

    public static void main(String[] args) {
        var primes = doIt(5000);

        for (int p : primes) {
            System.out.println(p);
        }

        if (primes.equals(SingleThreadPrime.primeFinder(5000))) {
            System.out.println("OK");
        } else {
            throw new RuntimeException("NOK");
        }
    }

}