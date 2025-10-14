package ticket.reserve.event.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.event.dto.EventUpdateRequestDto;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String eventTitle;          // 공연 제목
    private String description;         // 상세 내용
    private String location;            // 장소

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int totalSeats;             // 총 좌석 수
    private int availableSeats;         // 남은 좌석 수

    public void update(EventUpdateRequestDto request) {
        this.eventTitle = request.eventTitle();
        this.description = request.description();
        this.location = request.location();
        this.startTime = request.startTime();
        this.endTime = request.endTime();
        this.totalSeats = request.totalSeats();
        this.availableSeats = request.availableSeats();
    }
}
