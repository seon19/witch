package com.sp.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.admin.service.AdminHomeService;
import com.sp.app.common.MyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/*")
public class AdminHomeController {

	private final AdminHomeService service;
	private final MyUtil myUtil;
	
	@GetMapping("main")
	public String list() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("list: ", e);
		}
		
		return "admin/main";
	}
}
