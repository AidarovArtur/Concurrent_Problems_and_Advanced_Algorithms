/*
 * This file implements the final stage of the pipeline processing system. Key concepts: multi-stage processing, threading.
 */

import java.util.ArrayList;
import java.util.List;
// import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class PipeLine3 {
    static int NO_FURTHER_INPUT = Integer.MAX_VALUE;

    private static <T> List<T> nCopyList(int count, IntFunction<T> makeElem) {
        return IntStream.range(0, count).mapToObj(i -> makeElem.apply(i)).toList();
    }

    public static void main(String[] args) throws Exception {
        int bound = 100;
        int stageCount = 7;

        List<ArrayBlockingQueue<Integer>> queues = nCopyList(stageCount + 1, 
        n -> new ArrayBlockingQueue<Integer>(128)
         /* Create the nth queue */);

        initQueue(bound, queues.get(0));

        int[] queuedPrimes = new int[stageCount];

        List<Callable<List<Integer>>> callables = new ArrayList<Callable<List<Integer>>>();
        for (int i = 0; i < stageCount; i++) {
            int idx = i;
            callables.add(() -> {
                List<Integer> nonPrimes = new ArrayList<Integer>();

                try {
                    Integer element = queues.get(idx).take();
                    queuedPrimes[idx] = element;
                    // get the first number, the prime of the callable; put it in queuedPrimes


                    boolean isOn = true;
                    while (isOn) {
                        element = queues.get(idx).take();
                        if(element == Integer.MAX_VALUE){
                            queues.get(idx+1).put(element);
                            break;
                        }
                        if(element % queuedPrimes[idx] == 0){
                            nonPrimes.add(element);
                        }
                        else queues.get(idx+1).put(element);
                        // prev queue ====> num ===> nonPrimes  (if filtered out)
                        //                        \=====> next queue (if not filtered out)
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return nonPrimes;
            });
        }

        var pool = Executors.newCachedThreadPool();
        var futures = pool.invokeAll(callables);
        for (int i = 0; i < stageCount; i++) {
            System.out.printf("Filtered by %d: %s%n", queuedPrimes[i], futures.get(i).get());
        }

        var remainingPrimes = new ArrayList<>();
        queues.get(stageCount).drainTo(remainingPrimes);
        remainingPrimes.remove(remainingPrimes.size()-1);
        System.out.printf("Remaining: %s%n", remainingPrimes);

        pool.shutdown();
    }

    private static void initQueue(int bound, ArrayBlockingQueue<Integer> queue0) {
        // 3, 5, 7, ..., NO_FURTHER_INPUT ====> queue #0
        // List<Integer> data = List.of(3, 5, 7, 11, 13, 17, 19, 9, 15, 21, 27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87, 93, 99,
        // 25, 35, 55, 65, 85, 95, 49, 77, 91, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97);
        //     queue0.addAll(data);

            queue0.addAll(IntStream.range(2, bound+1).mapToObj(x -> Integer.valueOf(x)).toList());
            queue0.add(Integer.MAX_VALUE);
    }
}
