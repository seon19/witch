package com.sp.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.common.MyUtil;
import com.sp.app.service.WorkService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/work/*")
public class WorkroomController {
	
	private final WorkService service;
	private final MyUtil myUtil;

	@GetMapping("workroom")
	public String workroom() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("workroom: ", e);
		}
		
		return "work/workroom";
	}
}
