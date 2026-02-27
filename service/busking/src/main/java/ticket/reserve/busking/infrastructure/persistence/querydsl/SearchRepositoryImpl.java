package ticket.reserve.busking.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ticket.reserve.busking.application.SearchService;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.response.QBuskingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static org.springframework.util.StringUtils.hasText;
import static ticket.reserve.busking.domain.busking.QBusking.busking;
import static ticket.reserve.busking.domain.buskingimage.QBuskingImage.buskingImage;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchService {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BuskingResponseDto> search(BuskingSearchCondition condition) {
        NumberTemplate<Double> lat = Expressions.numberTemplate(Double.class, "ST_Y({0})", busking.coordinate);
        NumberTemplate<Double> lng = Expressions.numberTemplate(Double.class, "ST_X({0})", busking.coordinate);
        BooleanExpression isSubscribed = Expressions.asBoolean(false).as("isSubscribed");

        return queryFactory
                .from(busking)
                .leftJoin(busking.buskingImages, buskingImage)
                .where(titleLike(condition.title()),
                        locationEq(condition.location()),
                        timeGoe(condition.startTime()),
                        timeLoe(condition.endTime())
                )
                .orderBy(setOrderBy("createdAt"))
                .transform(
                        groupBy(busking.id).list(
                                new QBuskingResponseDto(
                                        busking.id,
                                        busking.title,
                                        busking.description,
                                        busking.location,
                                        busking.startTime,
                                        busking.endTime,
                                        list(buskingImage.storedPath),
                                        busking.userId,
                                        lat,
                                        lng,
                                        isSubscribed
                                )
                        )
                );
    }

    @Override
    public List<BuskingResponseDto> readAllWithCursor(Long lastBuskingId, int size) {
        return queryFactory
                .selectFrom(busking)
                .leftJoin(busking.buskingImages, buskingImage)
                .where(ltBuskingId(lastBuskingId))
                .orderBy(busking.id.desc())
                .limit(size)
                .fetch()
                .stream()
                .map(BuskingResponseDto::from)
                .toList();
    }

    private BooleanExpression ltBuskingId(Long lastBuskingId) {
        return lastBuskingId == null ? null : busking.id.lt(lastBuskingId);
    }

    private BooleanExpression titleLike(String title) {
        return hasText(title) ? busking.title.like("%"+title+"%") : null;
    }

    private BooleanExpression locationEq(String location) {
        return hasText(location) ? busking.location.eq(location) : null;
    }

    private BooleanExpression timeGoe(LocalDateTime startTime) {
        return startTime != null ? busking.startTime.goe(startTime) : null;
    }

    private BooleanExpression timeLoe(LocalDateTime endTime) {
        return endTime != null ? busking.endTime.loe(endTime) : null;
    }

    private OrderSpecifier setOrderBy(String sort) {
        Order direction = Order.DESC;
        PathBuilder<?> entityPath = new PathBuilder<>(busking.getType(), busking.getMetadata());
        try {
            return new OrderSpecifier(direction, entityPath.get(sort));
        } catch (Exception e) {
            return new OrderSpecifier(direction, busking.createdAt);
        }
    }
}
