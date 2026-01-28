package ticket.reserve.user.infrastructure.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class AtomicIntegerTest {

    @Test
    @DisplayName("int와 AtomicInteger의 동시성 비교")
    void concurrencyTest() throws Exception {
        int numberOfThreads = 10;
        int incrementsPerThread = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        ConcurrentTest test = new ConcurrentTest();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        test.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        System.out.println("예상 결과: " + (10 + (numberOfThreads * incrementsPerThread)));
        System.out.println("일반 int 결과: " + test.getCount());
        System.out.println("AtomicInteger 결과: " + test.getAtomicCount());
    }

    static class ConcurrentTest {
        private int count = 10;
        private final AtomicInteger atomicCount = new AtomicInteger(10);

        public void increment() {
            count++;
            atomicCount.incrementAndGet();
        }

        public int getCount() {
            return count;
        }

        public int getAtomicCount() {
            return atomicCount.get();
        }
    }
}
