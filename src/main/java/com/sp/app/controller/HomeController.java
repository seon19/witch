package com.sp.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.common.MyUtil;
import com.sp.app.service.HomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final HomeService service;
	private final MyUtil myUtil;
	
	@GetMapping("/")
	public String list() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("list: ", e);
		}
		
		return "main/home";
	}
	

}
