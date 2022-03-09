package ru.clevertec.custom_collection.my_list;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentLinkedListImplTest {
    private List<Integer> threadSafeList = new LinkedListImpl<>();
    private ExecutorService executorService;
    private static final int ITRRATIONS_COUNT = 100;
    private static final int NUM_WORKERS = 10;
    private CyclicBarrier cyclicBarrier;

    @Test
    void add_nTimes_nSize() {

        try {
            cyclicBarrier = new CyclicBarrier(NUM_WORKERS + 1);
            executorService = Executors.newCachedThreadPool();

            System.out.println("Spawning " + NUM_WORKERS
                    + " worker threads to compute "
                    + ITRRATIONS_COUNT + " partial results each");

            for (int i = 0; i < NUM_WORKERS; i++) {
                executorService.execute(new WorkerThread(LinkedListAction.ADD));
            }
            System.out.println("main is waiting for others to reach barrier");
            cyclicBarrier.await();
            System.out.println("Finish");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        assertEquals(ITRRATIONS_COUNT * NUM_WORKERS, threadSafeList.size());
    }
    @Test
    void remove_nTimes_emptyList(){
        try {
            cyclicBarrier = new CyclicBarrier(NUM_WORKERS + 1);
            executorService = Executors.newCachedThreadPool();
            IntStream.range(0, NUM_WORKERS * ITRRATIONS_COUNT)
                    .forEach(i -> {
                        if (i < ITRRATIONS_COUNT) {
                            threadSafeList.add(i);
                        } else {
                            threadSafeList.add(i % ITRRATIONS_COUNT);
                        }
                    });

            System.out.println("Spawning " + NUM_WORKERS
                    + " worker threads to compute "
                    + ITRRATIONS_COUNT + " partial results each");

            for (int i = 0; i < NUM_WORKERS; i++) {
                executorService.execute(new WorkerThread(LinkedListAction.REMOVE));
            }
            System.out.println("main is waiting for others to reach barrier");
            cyclicBarrier.await();
            System.out.println("Finish");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        assertEquals(0, threadSafeList.size());
    }

    @Test
    void set_uniqVal_allUniq(){
        try {
            cyclicBarrier = new CyclicBarrier(NUM_WORKERS + 1);
            executorService = Executors.newCachedThreadPool();
            threadSafeList.addAll
                    (Collections.nCopies(NUM_WORKERS * ITRRATIONS_COUNT, -1));

            List<Integer> expected = new ArrayListImpl<>();

            System.out.println("Spawning " + NUM_WORKERS
                    + " worker threads to compute "
                    + ITRRATIONS_COUNT + " partial results each");

            for (int i = 0; i < NUM_WORKERS; i++) {
                executorService.execute(new WorkerThread(LinkedListAction.SET));
            }
            System.out.println("main is waiting for others to reach barrier");
            cyclicBarrier.await();
            System.out.println("Finish");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        List<Integer> expected = new ArrayListImpl<>();
        IntStream.range(0, ITRRATIONS_COUNT)
                .forEach(expected::add);
        assertTrue(threadSafeList.containsAll(expected));
    }

    class WorkerThread implements Runnable {

        private LinkedListAction action;

        public WorkerThread(LinkedListAction action){
            this.action = action;
        }

        @Override
        public void run() {
            String thisThreadName = Thread.currentThread().getName();
            try {
                for (int i = 0; i < ITRRATIONS_COUNT; i++) {
                    switch (action) {
                        case ADD:
                            threadSafeList.add(i);
                            break;
                        case REMOVE:
                            Object o = new Integer(i);
                            threadSafeList.remove(o);
                            break;
                        case SET:
                            threadSafeList.set(i, i);
                            break;
                    }
                }
                System.out.println(thisThreadName
                        + " waiting for others to reach barrier.");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                // ...
            } catch (BrokenBarrierException e) {
                // ...
            }
        }
    }

    enum LinkedListAction{
        ADD, REMOVE, SET;
    }
}