package com.example.MCRound.Round1;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class StreamEventServiceImpl implements StreamEventService {

    private int NUM_LOCKS = 10;
    private ConcurrentHashMap<Integer, AtomicInteger> eventScores;
    private ConcurrentSkipListMap<Integer, Set<Integer>> scoreEventBuckets;
    private ReentrantLock[] reentrantLocks;

    public StreamEventServiceImpl() {
        eventScores = new ConcurrentHashMap<>();
        scoreEventBuckets = new ConcurrentSkipListMap<>();
        reentrantLocks = new ReentrantLock[NUM_LOCKS];
    }


    // checkand verify
    @Override
    public void increase(int id) {
        ReentrantLock reentrantLock = reentrantLocks[id % NUM_LOCKS];
        reentrantLock.lock();
        try {
            int newScore, oldScore = -1;
            if (eventScores.containsKey(id)) {
                oldScore = eventScores.get(id).get();
            } else {
                eventScores.put(id, new AtomicInteger());
            }
            newScore = eventScores.get(id).incrementAndGet();
            if (oldScore != -1) {
                scoreEventBuckets.get(oldScore).remove(id);
            }
            scoreEventBuckets.computeIfAbsent(newScore, key -> ConcurrentHashMap.newKeySet())
                    .add(id);
        } finally {
            reentrantLock.unlock();
        }
    }




    @Override
    public void decrease(int id) {
        ReentrantLock reentrantLock = reentrantLocks[id % NUM_LOCKS];
        reentrantLock.lock();
        try {
            int newScore, oldScore = -1;
            if (eventScores.containsKey(id)) {
                oldScore = eventScores.get(id).get();
            } else {
                return;
            }
            if (oldScore == 1) {
                eventScores.remove(id);
                newScore = 0;
            } else {
                newScore = eventScores.get(id).decrementAndGet();
            }
            if (oldScore != -1) {
                scoreEventBuckets.get(oldScore).remove(id);
            }
            if (newScore <= 0) {
                scoreEventBuckets.computeIfAbsent(newScore, key -> ConcurrentHashMap.newKeySet())
                        .add(id);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public int fetchMostPopular() {
        if (scoreEventBuckets.isEmpty()) return -1;
        Set<Integer> mostPopularItems = scoreEventBuckets.lastEntry().getValue();
        return mostPopularItems.iterator().next();
    }
}
