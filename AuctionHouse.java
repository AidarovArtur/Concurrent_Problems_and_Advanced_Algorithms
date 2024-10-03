/*
 * This file implements a multi-threaded auction system where multiple artists create NFTs, auctioneers manage auctions, and collectors place bids. It uses blocking queues for synchronizing auction offers and handling concurrent access. Key concepts: multi-threading, synchronization, concurrent data handling.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AuctionHouse{
    static class NFT {
        public final int artistIdx;
        public final int price;

        public NFT(int artistIdx, int price) {
            this.artistIdx = artistIdx;
            this.price = price;
        }
    }

    static class AuctionOffer {
        public int offeredSum;
        public String collectorName;

        public AuctionOffer(int offeredSum, String collectorName) {
            this.offeredSum = offeredSum;
            this.collectorName = collectorName;
        }
    }

    static int failCount = 0;

    static final int MAX_NFT_PRICE = 100;
    static final int MAX_NFT_IDX = 100_000;
    static final int MAX_COLLECTOR_OFFER = MAX_NFT_IDX / 100;

    private static final int COLLECTOR_MIN_SLEEP = 10;
    private static final int COLLECTOR_MAX_SLEEP = 20;
    private static final int MAX_AUCTION_OFFERS = 10;

    static final int ARTIST_COUNT = 10;
    static final int COLLECTOR_COUNT = 5;

    static final int INIT_ASSETS = MAX_NFT_IDX / 10 * MAX_NFT_PRICE;

    static int nftIdx = 0;
    static int remainingNftPrice = INIT_ASSETS;
    static NFT[] nfts = new NFT[MAX_NFT_IDX];

    static int totalCommission = 0;
    static int noAuctionAvailableCount = 0;
    static int soldItemCount = 0;

    //for Task 2: data structure "auctionQueue"
    static BlockingQueue<AuctionOffer> auctionQueue =new ArrayBlockingQueue<>(MAX_AUCTION_OFFERS);
    //for Task 3: data structure "owners"
    static List<String> owners = new ArrayList<>();


    public static void main(String[] args) throws InterruptedException {
        // Task 1
        List<Thread> artists = makeArtists();

        // Task 2
        Thread auctioneer = makeAuctioneer(artists);

        // Task 3
        List<Thread> collectors = makeCollectors(auctioneer);

        //make sure that everybody starts working
        for (int i = 0; i < ARTIST_COUNT; i++) {
            artists.get(i).start();
        }
        auctioneer.start();
        for(Thread t : collectors)
            t.start();
        //make sure that everybody finishes working
        try {
            for (int i = 0; i < ARTIST_COUNT; i++) {
                artists.get(i).join();
            }
            auctioneer.join();
            for(Thread t : collectors)
                t.join();
        } catch (InterruptedException e) {}


        runChecks();
    }

    // ------------------------------------------------------------------------
    // Task 1

    private static List<Thread> makeArtists() {
        //create ARTIST_COUNT artists as threads, all of whom do the following, and return them as a list
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < ARTIST_COUNT; i++) {
            final int finali = i;
            threads.add(new Thread(() -> {
                while (true) {
                    sleepForMsec(20);
                    int r = getRandomBetween(100, 1000);
                    int curIdx;
                    synchronized (nfts) {
                        if (nftIdx >= MAX_NFT_IDX) break;
                        if (r > remainingNftPrice) break;
                        curIdx = nftIdx++;
                        remainingNftPrice -= r;
                    }
                    nfts[curIdx] = new NFT(finali, r);
                }
            }));
        }
        return threads;
    }

    // ------------------------------------------------------------------------
    // Task 2

    private static boolean artistsWorking(List<Thread> artists)
    {
        boolean someoneWorking =false;
        for(Thread t : artists)
            someoneWorking = someoneWorking || t.isAlive();

        return someoneWorking;
    }
    private static Thread makeAuctioneer(List<Thread> artists) {
        //create and return the auctioneer thread that does the following
        return new Thread(() -> {
                while(artistsWorking(artists))
                {
                    makeAuction();
                    sleepForMsec(3);
                    auctionQueue = null;
                }
                for(int i = 0 ; i< 100; i++)
                {
                    makeAuction();
                    sleepForMsec(3);
                    auctionQueue = null;
                }

        });
        }
        public static void makeAuction()
        {
            int randNft = getRandomBetween(0,nftIdx);

            synchronized (nfts)
            {
                if(nfts[randNft] == null)
                {
                    return;
                }
            }

            if(auctionQueue == null)
                auctionQueue =new ArrayBlockingQueue<>(MAX_AUCTION_OFFERS);

            AuctionOffer max = null;
            for (int i = 0; i < MAX_AUCTION_OFFERS; i++) {
                AuctionOffer currOffer;
                    try {
                        currOffer = auctionQueue.poll(1, TimeUnit.MICROSECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                if (currOffer == null) {
                    continue;
                }
                if(max == null){
                    max = new AuctionOffer(currOffer.offeredSum, currOffer.collectorName);
                }else
                {
                    max = (max.offeredSum < currOffer.offeredSum) ? currOffer:max;
                }
            }
            if( max != null)
            {
                owners.add(max.collectorName);
                soldItemCount++;
                totalCommission += (nfts[randNft].price + max.offeredSum)*0.1;
            }

        }

    // ------------------------------------------------------------------------
    // Task 3

    private static List<Thread> makeCollectors(Thread auctioneer) {
        //create collectors now, the collectors' names are simply Collector1, Collector2, ...
        ArrayList<Thread> collectors = new ArrayList<>();
        for(int i = 0; i<COLLECTOR_COUNT ;i++)
        {
            int finalI = i;
            collectors.add(new Thread(()->{
                while(auctioneer.isAlive())
                {
                    sleepForMsec(getRandomBetween(COLLECTOR_MIN_SLEEP,COLLECTOR_MAX_SLEEP));
                        if(auctionQueue != null )
                        {
                            if(owners.contains("Collectors"+(finalI +1))){
                                continue;
                            }

                            auctionQueue.add(new AuctionOffer(getRandomBetween(1,MAX_COLLECTOR_OFFER),"Collectors"+(finalI +1)));

                        }
                        else
                        {
                            noAuctionAvailableCount++;
                        }
                }
            }));
        }
        // work until the auctioneer is done (it is not isAlive() anymore)
            // sleep for COLLECTOR_MIN_SLEEP..COLLECTOR_MAX_SLEEP milliseconds randomly between each step
        // if there is no auction available, just increase noAuctionAvailableCount
        // if there is an ongoing auction, and you haven't bid on it already, make an offer
            // choose your offer between 1..MAX_COLLECTOR_OFFER randomly
        return collectors;
    }

    // ------------------------------------------------------------------------
    // Tester

    private static String isOK(boolean condition) {
        if (!condition)   ++failCount;
        return isOkTxt(condition);
    }

    private static String isOkTxt(boolean condition) {
        return condition ? "GOOD" : "BAD ";
    }

    private static void runChecks() {
        if (Thread.activeCount() == 1) {
            System.out.printf("%s Only the current thread is running%n", isOK(true));
        } else {
            System.out.printf("%s %d threads are active, there should be only one%n", isOK(Thread.activeCount() == 1), Thread.activeCount());
        }

        System.out.printf("%s nftIdx > 0%n", isOK(nftIdx > 0));

        int soldPrice = IntStream.range(0, nftIdx).map(idx-> nfts[idx].price).sum();
        System.out.printf("%s Money is not lost: %d + %d = %d%n", isOK(soldPrice + remainingNftPrice == INIT_ASSETS), soldPrice, remainingNftPrice, INIT_ASSETS);

        System.out.printf("%s [Only Task 2] Total commission not zero: %d > 0%n", isOK(totalCommission > 0), totalCommission, INIT_ASSETS);

        System.out.printf("%s [Only Task 3] Sold item count not zero: %d > 0%n", isOK(soldItemCount > 0), soldItemCount, INIT_ASSETS);
        System.out.printf("%s [Only Task 3] Some collectors have become owners of NFTs: %d > 0%n", isOK(owners.size() > 0), owners.size(), INIT_ASSETS);
        System.out.printf("%s [Only Task 3] Sometimes, collectors found no auction: %d > 0%n", isOK(noAuctionAvailableCount > 0), noAuctionAvailableCount, INIT_ASSETS);

        System.out.printf("%s Altogether %d condition%s failed%n", isOkTxt(failCount == 0), failCount, failCount == 1 ? "" : "s");

        // forcibly shutting down the program (don't YOU ever do this)
        System.exit(42);
    }

    // ------------------------------------------------------------------------
    // Utilities

    private static int getRandomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    private static void sleepForMsec(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}