package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.tsid.IdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuskingService {

    private final BuskingCrudService buskingCrudService;
    private final InventoryPort inventoryPort;
    private final ImagePort imagePort;
    private final IdGenerator idGenerator;

    public BuskingResponseDto create(BuskingRequestDto request, MultipartFile file) {
        Busking busking = request.toEntity(idGenerator);

        // 이미지가 있다면 저장
        ImageResponseDto imageResponse = null;
        if (file != null && !file.isEmpty()) {
            imageResponse = imagePort.uploadImage(file);
            busking.addEventImage(
                    idGenerator, imageResponse.getOriginalFileName(), imageResponse.getStoredPath(),
                    ImageType.THUMBNAIL, 1
            );
        }

        // 버스킹 저장
        try {
            Busking savedBusking = buskingCrudService.save(busking);
            return BuskingResponseDto.from(savedBusking, savedBusking.getTotalInventoryCount());
        } catch (Exception e) {
            if (imageResponse != null) {
                imagePort.deleteImage(imageResponse.getImageId());
            }
            throw e;
        }
    }

    public List<BuskingResponseDto> getAll() {
        return buskingCrudService.findAll().stream()
                .map(e -> BuskingResponseDto.from(e, inventoryPort.countInventory(e.getId())))
                .toList();
    }

    public BuskingResponseDto getOne(Long buskingId) {
        Integer availableInventoryCount = inventoryPort.countInventory(buskingId);
        return BuskingResponseDto.from(buskingCrudService.findById(buskingId), availableInventoryCount);
    }
}
