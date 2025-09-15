package com.sp.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.common.MyUtil;
import com.sp.app.entity.Member;
import com.sp.app.service.HomeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main/*")
public class HomeController {
	
	private final HomeService service;
	private final MyUtil myUtil;
	
	@GetMapping("home")
	public String home(HttpSession session, Model model) throws Exception {
		
		try {
			Member loginUser = (Member) session.getAttribute("loginUser");
	        model.addAttribute("loginUser", loginUser);		
		} catch (Exception e) {
			log.info("home: ", e);
		}
		
		return "main/home";
	}
	

}
