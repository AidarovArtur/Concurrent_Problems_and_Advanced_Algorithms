/*
 * This file implements the Dining Philosophers problem, a classic concurrency problem, to prevent deadlock and ensure fair resource allocation between philosophers. Key concepts: deadlock prevention, concurrency.
 */

import java.util.concurrent.locks.ReentrantLock;
public class Philosophers {
    private static final int NUMBER_OF_PHILOSOPHERS = 5;
    private static final int THINK_TIME = 100;
    private static final int EAT_TIME = 50;

    private static ReentrantLock[] forks = new ReentrantLock[NUMBER_OF_PHILOSOPHERS];
    private static State[] state = new State[NUMBER_OF_PHILOSOPHERS];
    enum State{
        THINKING,
        HUNGRY, 
        EATING;
    };
    static {
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; ++i) {
            forks[i] = new ReentrantLock();
        }
    }

    private static class Philosopher extends Thread {
        private int id;

        Philosopher(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }

        private void test(int id){
            if( state[id] == Philosophers.State.HUNGRY && 
                state[(id + NUMBER_OF_PHILOSOPHERS - 1) % NUMBER_OF_PHILOSOPHERS] != Philosophers.State.EATING &&
                state[(id + 1) % NUMBER_OF_PHILOSOPHERS] != Philosophers.State.EATING){
                state[id] = Philosophers.State.EATING; 
                forks[id].unlock();
            }
        }

        private void think() {
            System.err.println("#" + id + " Thinking...");
            try {
                Thread.sleep(THINK_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void eat() {
            
            System.err.println("#" + id + " Taking left fork.");
            synchronized(Philosophers.class){
                state[id] = Philosophers.State.HUNGRY;
                test(id);
            }
            forks[id].lock();
            
            
            //synchronized (forks[id]) {
                System.err.println("#" + id + " Taking right fork.");
                //synchronized (forks[(id + 1) % NUMBER_OF_PHILOSOPHERS]) {
                    state[id] = Philosophers.State.EATING;
                    System.err.println("#" + id + " Eating...");
                    try {
                        Thread.sleep(EAT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                //}
            //}
                    
            synchronized(Philosopher.class){
                state[id] = Philosophers.State.THINKING;
                test((id + NUMBER_OF_PHILOSOPHERS - 1) % NUMBER_OF_PHILOSOPHERS);
                test((id + 1) % NUMBER_OF_PHILOSOPHERS);
            }
        }
    }

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];

        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; ++i) {
            philosophers[i] = new Philosopher(i);
            philosophers[i].start();
        }
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            try {
                philosophers[i].join();
            } catch (Exception e) {
            }
        }
    }
}