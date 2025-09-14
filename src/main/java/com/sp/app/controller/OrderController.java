package com.sp.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sp.app.common.MyUtil;
import com.sp.app.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders/*")
public class OrderController {
	
	private final OrderService service;
	private final MyUtil myUtil;
	
	@GetMapping("purchaseList")
	public String purchaseList() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("purchaseList: ", e);
		}
		
		return "orders/purchaseList";
	}
	

}
