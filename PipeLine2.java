/*
 * This file implements the third stage of the pipeline processing system. Key concepts: multi-stage processing, threading.
 */

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class PipeLine2 {
    private static <T> List<T> nCopyList(int count, IntFunction<T> makeElem) {
        return IntStream.range(0, count).mapToObj(i -> makeElem.apply(i)).toList();
    }

    public static void main(String[] args) throws Exception {
        List<Function<Integer, Integer>> funs = List.of(
            n -> n + 1,
            n -> 2 * n + 1,
            n -> -n
        );

        Integer NO_FURTHER_INPUT = Integer.MAX_VALUE;

        List<BlockingQueue<Integer>> queues = nCopyList(funs.size() + 1, 
        n -> new ArrayBlockingQueue<Integer>(64) 
        
            /* Created the nth queue */);

        var pool = Executors.newCachedThreadPool();

        pool.submit(() -> {
            var data = List.of(1, 2, 3, 4, 5, 6, 100);
            queues.get(0).addAll(data);
            queues.get(0).add(NO_FURTHER_INPUT);
        });

        for (int i = 0; i < funs.size(); i++) {
            int idx = i;
            pool.submit(() -> {
                try {
                    while (true) {
                        Integer element = queues.get(idx).take();
                        if(element.equals(NO_FURTHER_INPUT)) {
                            queues.get(idx+1).put(element);
                            break;
                        } 
                        queues.get(idx+1).put(funs.get(idx).apply(element));
                    
                        //  prev queue ====> num ===> use function, get new num ===> next queue
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        pool.submit(() -> {
            try {
                while (true) {
                    Integer element = queues.get(funs.size()).take();
                    if(element.equals(NO_FURTHER_INPUT)) break;
                    System.out.println(element);
                    // print the elements of the last queue
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}