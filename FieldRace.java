/*
 * This file implements a race simulation where multiple threads compete in a field. It demonstrates the use of multi-threading and synchronization for managing concurrent events. Key concepts: multi-threading, event synchronization.
 */

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.plaf.basic.BasicMenuUI.ChangeHandler;

public class FieldRace {
    static int PLAYER_COUNT, CHECKPOINT_COUNT;
    private static AtomicBoolean isOn = new AtomicBoolean();
    static Map<String, Integer> scores = new ConcurrentHashMap<>();
    static AtomicInteger[] checkpointScores = new AtomicInteger[PLAYER_COUNT];
    static List<BlockingQueue<AtomicInteger>> checkpointQueues = new ArrayList<>(); 
    static List<Future> ans = new ArrayList<>();
    public static void main(String[] args) {
        ExecutorService ex = Executors.newFixedThreadPool(CHECKPOINT_COUNT+PLAYER_COUNT+1);
        
        // "+1" thread prints the Scores: [1=494, 8=473, 4=456, 9=445, 2=431, 3=430, 5=368, 7=367, 6=360, 0=353]
        // if isOn == false it terminates
        
        for(int i = 0; i<CHECKPOINT_COUNT; i++){
            checkpointQueues.add(new ArrayBlockingQueue<AtomicInteger>(CHECKPOINT_COUNT));
        }
        for(int i = 0; i<PLAYER_COUNT; i++){
            checkpointScores[i] = new AtomicInteger(0);
            scores.put(String.valueOf(i), 0);
        }
        for(int i=0; i<PLAYER_COUNT; i++){
            ans.add(ex.submit(new Player(i)));
        }
        for (int i = 0; i < CHECKPOINT_COUNT; i++) {
            ans.add(ex.submit(new Checkpoint()));
        }

        try {
            Thread.sleep(10000);
            isOn.set(true);
            ex.shutdown();
            ex.awaitTermination(3, TimeUnit.SECONDS);
            ex.shutdownNow();
        } catch (InterruptedException e) {
        }
    }
    private class Player implements Runnable {
        private final int fI;
        Player(int i){
            fI = i;
        }
        public void run() {
            while (!isOn.get()) {
                int rand = ThreadLocalRandom.current().nextInt(0, CHECKPOINT_COUNT);
                sleepForSec(rand);
                BlockingQueue<AtomicInteger> temp;
                synchronized(checkpointQueues){
                    temp = checkpointQueues.get(rand);
                }
                AtomicInteger currScore = checkpointScores[fI];
                temp.add(currScore);
                synchronized(checkpointScores){
                    try {
                        checkpointScores.wait();
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }
                while(!isOn.get() && currScore.get() == 0){
                    synchronized (checkpointScores)
                    {
                        try {
                            checkpointScores.wait(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("PLAYER"+(fI+1) + " is waiting "+ currScore.get());
                    }
                }
                System.out.println("PLAYER"+(fI+1)+" got " + currScore.get() + " points at checkpoint " + (rand+1));
                scores.put(String.valueOf(fI), scores.get(String.valueOf(fI)) + currScore.get());

                checkpointScores[fI].set(0);
            }
            return;
        }
    }
    private class Checkpoint implements Runnable {
        public void run() {
            for(int i = 0; i < CHECKPOINT_COUNT; i++){
                final int fI = i;
            
                while (!isOn.get()) {
                    int rand = ThreadLocalRandom.current().nextInt(10, 100);
                    BlockingQueue<AtomicInteger> temp;
                    synchronized(checkpointQueues){
                        temp = checkpointQueues.get(fI);
                    }

                    AtomicInteger currScore = null;
                    synchronized(temp){
                        try {
                            currScore = temp.poll(2 , TimeUnit.SECONDS);

                            if(currScore == null){
                                continue;
                            }

                        } catch (InterruptedException e) {
                            try {
                                throw new InterruptedException();
                            } catch (InterruptedException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }

                    currScore.set(rand);

                    synchronized(checkpointScores){
                        checkpointScores.notify();
                    }
                }

            }
        }
    }
    private class ScoresPrinter implements Runnable{
        public void run(){
            synchronized(isOn){
                while(!isOn.get()){
                    System.out.println();
                    printScores();
                    System.out.println();
                }
            }
        }
    }
    private static void sleepForSec(int s){
        try {
            Thread.sleep(s);
        } catch (Exception e) {
        }
    }
    private static void printScores() {
        StringBuilder sb = new StringBuilder("Scores:[");
       
        System.out.println(sb.toString());
    }
}
