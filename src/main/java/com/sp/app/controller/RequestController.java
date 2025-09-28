package com.sp.app.controller;

import com.sp.app.dto.UserRequestDTO;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.UserRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final UserRequestService userRequestService;


    @GetMapping("/receive")
    public String receive(@RequestParam(name = "page", defaultValue = "1") int page,
                          @RequestParam(name = "size", defaultValue = "6") int size,
                          HttpSession session, Model model) {

        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) return "redirect:/member/login";

        int p = Math.max(0, page - 1);
        int s = Math.max(1, size);

        Pageable pageable = PageRequest.of(p, s);
        Page<UserRequestDTO> result = userRequestService.availableForUser(info.getMemberId(), pageable);

        if (p >= result.getTotalPages() && result.getTotalPages() > 0) {
            pageable = PageRequest.of(result.getTotalPages() - 1, s);
            result = userRequestService.availableForUser(info.getMemberId(), pageable);
        }

        model.addAttribute("cards", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("pageNumber", result.getNumber() + 1);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("pageSize", result.getSize());

        return "request/receive";
    }

    @PostMapping("/accept")
    public String accept(@RequestParam(name = "id") Long requestId,
                         HttpSession session, RedirectAttributes ra) {
        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) return "redirect:/member/login";

        try {
            userRequestService.accept(info.getMemberId(), requestId);
            ra.addFlashAttribute("msg", "의뢰를 수락했습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "의뢰 수락 중 오류가 발생했습니다.");
        }
        return "redirect:/request/receive";
    }

    @GetMapping("/api/ongoing")
    @ResponseBody
    public List<UserRequestDTO> ongoing(HttpSession session) {
        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userRequestService.ongoingForHome(info.getMemberId());
    }

    @GetMapping("/api/{requestListId}")
    @ResponseBody
    public UserRequestDTO detail(@PathVariable("requestListId") Long requestListId,
                                 HttpSession session) {
        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userRequestService.detail(requestListId, info.getMemberId());
    }

    @PostMapping("/api/{requestListId}/claim")
    @ResponseBody
    public UserRequestDTO claim(@PathVariable("requestListId") Long requestListId,
                                HttpSession session) {
        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userRequestService.claimReward(requestListId, info.getMemberId());
    }
}