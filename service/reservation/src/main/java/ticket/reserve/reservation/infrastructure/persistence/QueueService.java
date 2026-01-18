package ticket.reserve.reservation.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.dto.response.QueueStatusResponseDto;
import ticket.reserve.reservation.application.port.out.BuskingPort;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final BuskingPort buskingPort;
    private final QueueRedisRepository queueRedisRepository;

    private static final long ALLOW_COUNT = 10L;       // 한 번에 입장시킬 인원 수 (조절 가능)
    private static final long ACTIVE_TTL = 2*60*1000L;  // ACTIVE 상태 유지 시간 (2분 뒤 만료)

    // 대기열 등록
    public QueueStatusResponseDto registerWaitingQueue(Long buskingId, String userId) {
        if (queueRedisRepository.getActiveScore(buskingId, userId) != null) {
            return QueueStatusResponseDto.active();
        }

        long currentTimeMills = System.currentTimeMillis();
        queueRedisRepository.addToWaitingQueue(buskingId, userId, currentTimeMills);

        Long rank = queueRedisRepository.getWaitingRank(buskingId, userId);

        return QueueStatusResponseDto.waiting(rank+1);
    }

    // 앞의 대기 인원 수 조회
    public QueueStatusResponseDto getQueueStatus(Long buskingId, String userId) {
        if (queueRedisRepository.getActiveScore(buskingId, userId) != null) {
            return QueueStatusResponseDto.active();
        }

        Long rank = queueRedisRepository.getWaitingRank(buskingId, userId);
        if (rank != null) {
            return QueueStatusResponseDto.waiting(rank+1);
        }

        throw new CustomException(ErrorCode.QUEUE_TOKEN_NOT_FOUND);
    }

    // 진입 가능 여부 반환
    public boolean isAllowed(Long buskingId, Long userId) {
        return (queueRedisRepository.getActiveScore(buskingId, String.valueOf(userId))) != null;
    }

    // 스케줄링 메서드 - 3초마다 실행
    public void processQueue() {
        List<Long> buskingIds = buskingPort.getIds();
        long currentTimeMillis = System.currentTimeMillis();

        for (Long buskingId : buskingIds) {
            // 만료 시간이 현재 시간보다 작은 사용자들의 ACTIVE 상태를 삭제
            queueRedisRepository.removeActiveBeforeExpiryTime(buskingId, currentTimeMillis);

            // 대기열에서 선착순 ALLOW_COUNT 수만큼 조회
            Set<String> usersToEnter = queueRedisRepository.getOldestWaitingUsers(buskingId, ALLOW_COUNT);
            if (usersToEnter == null || usersToEnter.isEmpty()) continue;

            log.info("[QueueService.processQueue] {}명 입장 처리 중...", usersToEnter.size());

            // 입장열로 이동
            long expireTime = System.currentTimeMillis() + ACTIVE_TTL;
            for (String userId : usersToEnter) {
                queueRedisRepository.addToActiveQueue(buskingId, userId, expireTime);
                queueRedisRepository.removeToWaitingQueue(buskingId, userId);
            }
        }
    }
}
