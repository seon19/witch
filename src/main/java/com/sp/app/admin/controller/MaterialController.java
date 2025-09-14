package com.sp.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.admin.service.MaterialService;
import com.sp.app.common.MyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/material/*")
public class MaterialController {

	private final MaterialService service;
	private final MyUtil myUtil;
	
	@GetMapping("materialList")
	public String list() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("list: ", e);
		}
		
		return "admin/material/materialList";
	}
}
