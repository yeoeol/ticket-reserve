package ticket.reserve.busking.infrastructure.persistence.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.busking.domain.buskingimage.BuskingImage;
import ticket.reserve.busking.domain.buskingimage.QBuskingImage;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

import static ticket.reserve.busking.domain.busking.QBusking.busking;
import static ticket.reserve.busking.domain.buskingimage.QBuskingImage.buskingImage;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SearchRepositoryImplTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Autowired
    BuskingRepository buskingRepository;

    @Autowired
    SearchRepositoryImpl searchRepository;

    @Autowired
    IdGenerator idGenerator;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Busking busking1 = createBusking("title1", "desc1", "loc1",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10);
        busking1.addEventImage(
                idGenerator, "image1", "stored1",
                ImageType.THUMBNAIL, 1
        );
        Busking busking2 = createBusking("title2", "desc2", "loc2",
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), 20);
        busking2.addEventImage(
                idGenerator, "image2", "stored2",
                ImageType.THUMBNAIL, 1
        );
        Busking busking3 = createBusking("title3", "desc3", "loc3",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), 30);
        busking3.addEventImage(
                idGenerator, "image3", "stored3",
                ImageType.THUMBNAIL, 1
        );
        buskingRepository.saveAll(List.of(busking1, busking2, busking3));
    }

    private Busking createBusking(
            String title, String description, String location,
            LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount
    ) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point coordinate = geometryFactory.createPoint(new Coordinate(0, 0));

        return Busking.create(
                idGenerator, title, description, location,
                startTime, endTime, totalInventoryCount, coordinate
        );
    }

    @Test
    public void test() {
        List<Busking> buskings = queryFactory
                .select(busking)
                .from(busking)
                .leftJoin(busking.buskingImages, buskingImage)
                .where(busking.startTime.goe(LocalDateTime.of(2026, 1, 23, 0, 24, 32)))
                .fetch();

        for (Busking busking : buskings) {
            printLog(busking);
        }
    }

    @Test
    public void searchTest() {
        BuskingSearchCondition condition = new BuskingSearchCondition(
                "titl", null, null, null
        );
        List<BuskingResponseDto> result = searchRepository.search(condition);
        for (BuskingResponseDto busking : result) {
            System.out.println(busking);
        }
    }

    public void printLog(Busking busking) {
        System.out.println("========================");
        System.out.println("title = " + busking.getTitle());
        System.out.println("description = " + busking.getDescription());
        System.out.println("location = " + busking.getLocation());
        System.out.println("startTime = " + busking.getStartTime());
        System.out.println("endTime = " + busking.getEndTime());
        System.out.println("totalInventoryCount = " + busking.getTotalInventoryCount());
        printImageLog(busking.getBuskingImages());
        System.out.println("========================");
    }

    private void printImageLog(List<BuskingImage> buskingImages) {
        for (BuskingImage image : buskingImages) {
            System.out.println("originalFileName = " + image.getOriginalFileName());
            System.out.println("storedPath = " + image.getStoredPath());
        }
    }
}