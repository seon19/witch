package com.sp.app.admin.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.admin.service.PotionService;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Potion;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/postion/*")
public class PotionController {
	private final PotionService service;
	private final MyUtil myUtil;
	
	@GetMapping("postionList")
	public String list(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model,
			HttpSession session) throws Exception {
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");  
	        
			kwd = myUtil.decodeUrl(kwd);
			
			int total_page = 0;
			int size = 10;
			long dataCount = 0;
			List<Potion> list = null;
			
			Page<Potion> pagePostion = service.listPage(schType, kwd, current_page, size);
			
			if(pagePostion.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pagePostion.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pagePostion = service.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pagePostion.getTotalElements();
				
				list = pagePostion.getContent();
			}			
			
			model.addAttribute("loginUser", loginUser);  
			model.addAttribute("list", list);
			model.addAttribute("page", current_page);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
		} catch (Exception e) {
			log.info("materialList: ", e);
		}
		return "admin/postion/postionList";
	}
	
	@GetMapping("postionWrite")
	public String writeForm(Model model) throws Exception{
		model.addAttribute("mode", "write");
		return "admin/postion/postionWrite";
	}
	
	@PostMapping("write")
	public String writeSubmit(Potion dto, HttpServletRequest req) throws Exception{
		try {
			service.insertPotion(dto);
		} catch (Exception e) {
			log.info("writeSubmit : " , e);
		}		
		return "redirect:/potion/postionList";
	}
}
