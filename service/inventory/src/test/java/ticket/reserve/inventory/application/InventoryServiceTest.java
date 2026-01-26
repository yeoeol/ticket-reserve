package ticket.reserve.inventory.application;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.inbox.InboxRepository;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class InventoryServiceTest {

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InboxRepository inboxRepository;

    Inventory inventory = null;
    final int numberOfThreads = 100;

    private List<Long> inventoryIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        inventory = Inventory.create(() -> 1L, 1L, "TEST_001", 1000);
    }

    // 테스트 격리를 위해 수동으로 데이터를 삭제
    @AfterEach
    void tearDown() {
        inventoryIds.forEach(id -> {
            inventoryRepository.deleteById(id);
        });
    }

    /**
     * Feature: 좌석 선점 동시성 테스트
     * Background
     *      Given TEST_001 라는 이름의 좌석이 등록되어 있음
     * <p>
     * Scenario: 특정 좌석을 100명의 사용자가 동시에 접근하여 선점 요청
     *              Lock의 이름은 좌석ID
     * </p>
     * Then 사용자들의 요청 중 한 개의 요청만 실행되고 다른 요청들은 대기하다가 실패해야 함
     */

    @Test
    @DisplayName("좌석선점_분산락_적용_동시성100명_테스트")
    void 좌석선점_분산락_적용_동시성100명_테스트() throws InterruptedException {
        inventoryRepository.save(inventory);

        AtomicInteger failCount = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 분산락 적용 메서드 호출
                    inventoryService.holdInventory(1L, inventory.getId());
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Inventory persistedInventory = inventoryRepository.findById(inventory.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertThat(persistedInventory.getStatus().name()).isEqualTo("PENDING");
        assertThat(failCount.get()).isEqualTo(numberOfThreads-1);

        printLog(failCount, persistedInventory);

        inventoryIds.add(persistedInventory.getId());
    }

    @Test
    @DisplayName("좌석선점_비관적락_적용_동시성100명_테스트")
    void 좌석선점_비관적락_적용_동시성100명_테스트() throws InterruptedException {
        inventoryRepository.saveAndFlush(inventory);

        AtomicInteger failCount = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 비관적락 적용 메서드 호출
                    inventoryService.holdInventoryV2(inventory.getId());
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Inventory persistedInventory = inventoryRepository.findById(inventory.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertThat(persistedInventory.getStatus().name()).isEqualTo("PENDING");
        assertThat(failCount.get()).isEqualTo(numberOfThreads-1);

        printLog(failCount, persistedInventory);

        inventoryIds.add(persistedInventory.getId());
    }

    @Test
    @DisplayName("좌석선점_분산락_미적용_동시성100명_테스트")
    void 좌석선점_분산락_미적용_동시성100명_테스트() throws InterruptedException {
        inventoryRepository.saveAndFlush(inventory);

        AtomicInteger failCount = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 미적용 메서드 호출
                    inventoryService.holdInventoryV1(1L, inventory.getId());
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Inventory persistedInventory = inventoryRepository.findById(inventory.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertThat(persistedInventory.getStatus().name()).isEqualTo("PENDING");
        assertThat(failCount.get()).isNotEqualTo(numberOfThreads-1);

        printLog(failCount, persistedInventory);

        inventoryIds.add(persistedInventory.getId());
    }

    @Test
    @DisplayName("카프카 중복 이벤트 동시성100개 테스트")
    void 카프카_중복_이벤트_동시성100개_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Event<EventPayload> event = createEvent();

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    inventoryService.handleEvent(event);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        List<Inventory> inventoryList = inventoryRepository.findByBuskingId(1L);
        assertThat(inventoryList).hasSize(10);
        assertThat(inboxRepository.existsByEventId(event.getEventId())).isTrue();

        inventoryList.stream()
                .map(Inventory::getId)
                .forEach(id -> inventoryIds.add(id));
    }

    private static Event<EventPayload> createEvent() {
        return Event.of(1234L, EventType.BUSKING_CREATED,
                BuskingCreatedEventPayload.builder()
                        .buskingId(1L)
                        .title("testTitle")
                        .description("testDesc")
                        .location("testLoc")
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusDays(1))
                        .totalInventoryCount(10)
                        .build()
        );
    }

    private void printLog(AtomicInteger failCount, Inventory persistedInventory) {
        System.out.println("=================================");
        System.out.println("Total Attempts: " + numberOfThreads);
        System.out.println("Success Count: " + (numberOfThreads - failCount.get()));
        System.out.println("Fail Count: " + failCount.get());
        System.out.println("Final Status: " + persistedInventory.getStatus());
        System.out.println("=================================");
    }
}