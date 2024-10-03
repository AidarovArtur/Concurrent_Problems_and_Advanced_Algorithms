/*
 * This file implements a synchronized list to manage concurrent modifications by multiple threads. Key concepts: synchronization, concurrent data structures.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;

public class SyncList
{
    public static void nonSyncIterate(int threadNum, Collection<Integer> list)
    {
        for (Iterator<Integer> it = list.iterator(); it.hasNext(); ) {
            Integer x = it.next();
            System.out.println(threadNum + " " + x);
        }
        /*for (Integer x : list) {
            System.out.println(threadNum + " " + x);
        }*/
    }
    public static void syncIterate(int threadNum, Collection<Integer> list)
    {
        synchronized (list)
        {
            nonSyncIterate(threadNum, list);
        }
    }
    public static void main(String[] args)
    {
        ArrayList<Collection<Integer>> allCollections = new ArrayList<>();
        ArrayList<Integer> al = new ArrayList<>();
        LinkedList<Integer> ll = new LinkedList<>();
        Vector<Integer> v = new Vector<>();
        for (int i = 0; i < 100_000; i++) {
            al.add(i);
            ll.add(i);
            v.add(i);
        }
        allCollections.add(al); allCollections.add(ll); allCollections.add(v);
        allCollections.add(Collections.synchronizedCollection(al));
        allCollections.add(Collections.synchronizedCollection(ll));
        allCollections.add(Collections.synchronizedCollection(v));
        allCollections.add(Collections.synchronizedList(al));
        allCollections.add(Collections.synchronizedList(ll));
        allCollections.add(Collections.synchronizedList(v));
        for (Collection<Integer> coll : allCollections) {
            for (int j = 0; j < 2; j++) {
                final int finalj = j;
                Thread thread1 = new Thread(() -> {
                    if (finalj == 0) nonSyncIterate(1, coll);
                    else syncIterate(1, coll);
                });
                Thread thread2 = new Thread(() -> {
                    if (finalj == 0) nonSyncIterate(2, coll);
                    else syncIterate(2, coll);
                });
                thread1.start(); thread2.start();
                try {
                    thread1.join(); thread2.join();
                } catch (InterruptedException e) {}
                System.console().readLine();
            }
        }
    }
}