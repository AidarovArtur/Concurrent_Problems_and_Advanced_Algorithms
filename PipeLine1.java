/*
 * This file implements the second stage of the pipeline processing system. Key concepts: multi-stage processing, threading.
 */

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class PipeLine1 {
    public static void main(String[] args) throws Exception {
        String NO_FURTHER_INPUT1 = "";
        Integer NO_FURTHER_INPUT2 = Integer.MAX_VALUE; //-1;

        BlockingQueue<String> bq1 = new ArrayBlockingQueue<String>(64);//  create the queue
        LinkedBlockingQueue<Integer> bq2 = new LinkedBlockingQueue<Integer>();//  create the queue

        ExecutorService pool = Executors.newCachedThreadPool();

        pool.submit(() -> {
            bq1.addAll(List.of("a", "bb", "ccccccc", "ddd", "eeee", NO_FURTHER_INPUT1));
        });

        pool.submit(() -> {
            try {
                while (true) {
                    String element = bq1.take();
                    bq2.put(element.equals(NO_FURTHER_INPUT1) ? NO_FURTHER_INPUT2 : element.length());
                    if(element.equals(NO_FURTHER_INPUT1)) break;
                    //  queue #1 ====> txt  len ===> queue #2
                    //  also handle NO_FURTHER_INPUTs
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        pool.submit(() -> {
            try {
                while (true) {
                    Integer element = bq2.take();
                    if(element.equals(NO_FURTHER_INPUT2) ) break;
                    System.out.println(element);
                    //  queue #2 ====> len ====> print it
                    //  also handle NO_FURTHER_INPUTs
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
