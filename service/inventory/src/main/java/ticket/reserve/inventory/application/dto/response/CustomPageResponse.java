package ticket.reserve.inventory.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageResponse<T> {

    private List<T> content;       // 실제 데이터 리스트
    private int pageNumber;        // 현재 페이지 번호
    private int pageSize;          // 페이지 크기
    private long totalElements;    // 전체 데이터 수
    private int totalPages;        // 전체 페이지 수
    private boolean first;         // 첫 페이지 여부
    private boolean last;          // 마지막 페이지 여부

    public static <T> CustomPageResponse<T> from(Page<T> page) {
        return new CustomPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
