package ticket.reserve.busking.application.dto.request;

import jakarta.validation.constraints.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;

public record BuskingRequestDto(
        @NotBlank(message = "{busking.title.not_blank}")
        @Size(max = 100, message = "{busking.title.range}")
        String title,
        @NotBlank(message = "{busking.description.not_blank}")
        String description,
        @NotBlank(message = "{busking.location.not_blank}")
        String location,

        @NotNull(message = "{busking.startTime.not_null}")
        LocalDateTime startTime,
        @NotNull(message = "{busking.endTime.not_null}")
        LocalDateTime endTime,

        @NotNull(message = "{location.latitude.not_null}")
        @DecimalMin(value = "-90.0", message = "{location.latitude.range}")
        @DecimalMax(value = "90.0", message = "{location.latitude.range}")
        Double latitude,
        @NotNull(message = "{location.longitude.not_null}")
        @DecimalMin(value = "-180.0", message = "{location.longitude.range}")
        @DecimalMax(value = "180.0", message = "{location.longitude.range}")
        Double longitude
) {
    public Busking toEntity(IdGenerator idGenerator) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point coordinate = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return Busking.create(
                idGenerator, this.title, this.description, this.location,
                this.startTime, this.endTime, coordinate
        );
    }
}
