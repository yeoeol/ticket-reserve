package ticket.reserve.busking.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.BuskingService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buskings")
public class BuskingController {

    private final BuskingService buskingService;

    @GetMapping
    public String getAll(Model model) {
        List<BuskingResponseDto> buskingList = buskingService.getAll();
        model.addAttribute("buskingList", buskingList);

        return "busking-list";
    }

    @GetMapping("/{id}")
    public String getOne(
            @PathVariable("id") Long buskingId,
            @AuthenticationPrincipal String userId,
            Model model
    ) {
        BuskingResponseDto busking = buskingService.getOne(buskingId, Long.valueOf(userId));
        model.addAttribute("busking", busking);
        model.addAttribute("isAuthenticated", userId != null);

        return "busking-detail";
    }

    @GetMapping("/new")
    public String createPage() {
        return "busking-create";
    }

    @PostMapping
    public String create(
            BuskingRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        BuskingResponseDto response = buskingService.create(request, file);
        return "redirect:/buskings/%d".formatted(response.id());
    }
}
