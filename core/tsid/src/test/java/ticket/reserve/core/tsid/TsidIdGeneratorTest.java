package ticket.reserve.core.tsid;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class TsidIdGeneratorTest {

    IdGenerator idGenerator = new TsidIdGenerator();

    @Test
    void nextIdTest() throws Exception {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<Long>>> futures = new ArrayList<>();

        int repeatCount = 1000;
        int idCount = 1000;

        //when
        for (int i = 0; i < repeatCount; i++) {
            futures.add(executorService.submit(
                    () -> generateIdList(idGenerator, idCount))
            );
        }

        //then
        List<Long> result = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            List<Long> idList = future.get();
            for (int i = 1; i < idList.size(); i++) {
                assertThat(idList.get(i)).isGreaterThan(idList.get(i-1));
            }
            result.addAll(idList);
        }
        assertThat(result.stream().distinct().count()).isEqualTo(repeatCount*idCount);

        executorService.shutdown();
    }

    private List<Long> generateIdList(IdGenerator idGenerator, int count) {
        List<Long> idList = new ArrayList<>();
        while (count-- > 0) {
            idList.add(idGenerator.nextId());
        }
        return idList;
    }

    @Test
    void nextIdPerformanceTest() throws Exception {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int repeatCount = 1000;
        int idCount = 1000;
        CountDownLatch latch = new CountDownLatch(repeatCount);

        //when
        long start = System.nanoTime();
        for (int i = 0; i < repeatCount; i++) {
            executorService.submit(() -> {
                generateIdList(idGenerator, idCount);
                latch.countDown();
            });
        }

        latch.await();

        long end = System.nanoTime();
        System.out.println("times = %s ms".formatted((end - start) / 1_000_000));

        executorService.shutdown();
    }
}