/*
 * This file contains a solution to a general problem involving algorithm design and optimization. Key concepts: problem-solving, algorithm design.
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Solution1
{
    /*static class CyclicBarrier
    {
        int parties;
        Runnable barrierAction;
        AtomicInteger val = new AtomicInteger(0);
        CyclicBarrier(int parties)
        {
            this.parties = parties;
        }
        CyclicBarrier(int parties, Runnable barrierAction) {
            this.parties = parties;
            this.barrierAction = barrierAction;
        }
        boolean shootArrow() {
            synchronized (Solution1.class) {
                if (enemy <= 0) return false;
                if (val.incrementAndGet() == parties) {
                    if (barrierAction != null) {
                        barrierAction.run();
                    } else {
                        for (int i = 0; i < parties; i++) {
                            b.add(String.format("Enemy hit by arrow %d\n", --enemy));
                        }
                    }
                    val.set(0);
                }
            }
            Solution1.shootArrow();
            return true;
        }
    }*/

  private static int enemy     = 200;
  private static int tickRate  = 100;
  private static BlockingQueue<String> b = new ArrayBlockingQueue<String>(10);

  private static void shootArrow() {
    delay(1);
    //enemy--;
    //System.out.printf("Enemy hit by arrow %d\n", curEnemy);
  }

  private static void delay(int ticks) {
    try {Thread.sleep(ticks * tickRate);}
    catch (Exception e) {e.printStackTrace();}
  }

  public static void main(String[] args) {
    Thread threads[] = new Thread[10+1+2];
    //CyclicBarrier cb = new CyclicBarrier(10);
    CyclicBarrier cb = new CyclicBarrier(10, () -> {
        synchronized (Solution1.class) {
            enemy -= 10;
            b.add(String.format("Enemies hit by arrow %d-%d\n", enemy, enemy+9));
        }
    });
    for (int i = 0; i < 10; i++) {
        threads[i] = new Thread(() -> {
            while (true) {
                if (enemy <= 0) break;
                try {
                    cb.await();
                } catch (InterruptedException|BrokenBarrierException e) {}
                /*synchronized (Solution1.class) {
                    if (enemy <= 0) break;
                    b.add(String.format("Enemy hit by arrow %d\n", --enemy));
                }*/
                shootArrow();
                delay(5);
            }
        });
    }
    threads[10] = new Thread(() -> {
        while (true) {
            /*synchronized (Solution1.class) {
                if (enemy <= 0) break;
            }*/
            try {
                String message = b.poll(tickRate, TimeUnit.MILLISECONDS);
                if (message == null) {
                    if (enemy <= 0) break;
                    continue;
                }
                System.out.println(message);
            } catch (InterruptedException e) {}
        }
        /*String m;
        try {
            while ((m = b.poll(0, TimeUnit.SECONDS)) != null) System.out.println(m);
        } catch (InterruptedException e) {}*/
    });
    AtomicBoolean haveCannonball = new AtomicBoolean(false);
    threads[11] = new Thread(() -> {
        while (true) {
            if (haveCannonball.get()) {
                if (enemy <= 0) break;
                delay(1);
                continue;
            }
            delay(10); //smuggling
            haveCannonball.set(true);
            synchronized (haveCannonball) {
                haveCannonball.notify();
            }
        }
    }); //smuggler
    threads[12] = new Thread(() -> {
        while (true) {
            if (enemy <= 0) break;
            synchronized (haveCannonball) {
                try {
                    haveCannonball.wait(tickRate);
                } catch (InterruptedException e) {}
                if (!haveCannonball.get()) continue;
            }
            haveCannonball.set(false);
            synchronized (Solution1.class) {
                if (enemy < 10) enemy = 0;
                enemy -= 10;
            }
        }
    }); //cannon
    for (int i = 0; i < threads.length; i++) threads[i].start();
    try {
        for (int i = 0; i < threads.length; i++) threads[i].join();
    } catch (InterruptedException e) {}
  }

}