/*
 * This file implements the classic BarberShop problem, which demonstrates the coordination between multiple customer threads and a barber using synchronization techniques. Key concepts: synchronization, resource management.
 */

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BarberShop {
    private static final int WAITING_ROOM_SIZE = 5;
    private static final int MAX_CUSTOMERS = 30;

    private static Set<Customer> waitingRoom = new HashSet<>();
    private static boolean open = true;

    private static class Barber extends Thread {
        // private boolean awake = true;

        // public boolean isAwake() {
        //     return awake;
        // }

        @Override
        public void run() {
            while(open) {
                Customer nextCustomer = null;

                synchronized (waitingRoom) {
                    if (!waitingRoom.isEmpty()) {
                        nextCustomer = waitingRoom.iterator().next();
                        waitingRoom.remove(nextCustomer);
                    }
                }

                if (nextCustomer != null) {
                    System.out.println("Barber: Cutting hair of customer " + nextCustomer.getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.err.println("Barber: Oh! I am fired!");
                        return;
                    }
                    System.out.println("Barber: Hair of customer " + nextCustomer.getName() + " is cut.");
                } else {
                    //Sleep until next customer arrives.
                    synchronized(waitingRoom){
                        try {
                            waitingRoom.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static Barber barber = new Barber();

    private static class Customer extends Thread {
        public Customer(String name) {
            setName(name);
        }

        @Override
        public void run() {
            synchronized (waitingRoom) {
                if (waitingRoom.size() < WAITING_ROOM_SIZE) {
                    System.err.println("                                                  "
                            + getName() + ": Sitting in the waiting room.");
                    waitingRoom.add(this);
                    if (waitingRoom.size() == 1) {
                        // Wake up the barber if he is sleeping.
                        synchronized(waitingRoom){
                            waitingRoom.notify();
                        }
                    }
                } else {
                    System.err.println("                                                  "
                            + getName() + ": Leaving.");
                }
            }
        }
    }

    public static void main(String args[]) {
        Random rand = new Random();
        
        barber.start();
        for (int i = 0; i < MAX_CUSTOMERS; ++i) {
            Customer customer = new Customer("Customer" + i);
            customer.start();
            try {
                Thread.sleep(rand.nextInt(2500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        open = false;
        try {
            Thread.sleep(rand.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        barber.interrupt();
    }
}