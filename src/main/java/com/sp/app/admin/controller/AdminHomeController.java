package com.sp.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.entity.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller	
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/*")
public class AdminHomeController {
	
	@GetMapping("main")
	public String list(HttpSession session, Model model) {
	    try {
	        Member loginUser = (Member) session.getAttribute("loginUser");
	        model.addAttribute("loginUser", loginUser);
	        
	    } catch (Exception e) {
	        log.error("list error: ", e);
	    }

	    return "admin/main";
	}
	

}
