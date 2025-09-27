package com.sp.app.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.dto.UserRequestDTO;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.UserRequestService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
	private final UserRequestService userRequestService;
	@GetMapping("/receive")
	public String receive(@RequestParam(name = "page", defaultValue = "1") int page,
	                      HttpSession session, Model model) {

	    SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
	    if (info == null) return "redirect:/member/login";

	    Pageable pageable = PageRequest.of(Math.max(0, page - 1), 3);
	    Page<UserRequestDTO> p = userRequestService.availableForUser(info.getMemberId(), pageable);

	    model.addAttribute("cards", p.getContent()); 
	    model.addAttribute("page", p);
	    return "request/receive";
	}

	@PostMapping("/accept")
	public String accept(@RequestParam(name = "id") Long requestId,
	                     HttpSession session, RedirectAttributes ra) {
	    SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
	    userRequestService.accept(info.getMemberId(), requestId);
	    ra.addFlashAttribute("msg", "의뢰를 수락했습니다.");
	    return "redirect:/request/receive";
	}
}
