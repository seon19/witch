package com.sp.app.admin.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sp.app.admin.service.MaterialService;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Material;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
			List<Material> list = null;
			
			Page<Material> pageMaterial = service.listPage(schType, kwd, current_page, size);
			
			if(pageMaterial.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pageMaterial.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pageMaterial = service.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pageMaterial.getTotalElements();
				
				list = pageMaterial.getContent();
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
		
		return "admin/material/materialList";
	}
	
	@GetMapping("write")
	public String writeForm(Model model, HttpSession session) throws Exception {
		
		model.addAttribute("mode", "write");
		
		return "admin/material/materialWrite";
	}
	
	@PostMapping("write")
	public String writeSubmit(Material dto, HttpServletRequest req, @RequestParam("selectFile") MultipartFile selectFile) throws Exception {
		
		try {
			service.insertMaterial(dto, selectFile);
			
		} catch (Exception e) {
			log.info("writeSubmit: ", e);
		}
		
		return "redirect:/admin/material/materialList";
	}
	
	@GetMapping("materialDetail/{materialId}")
	public String materialDetail(@PathVariable("materialId") long materialId, @RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			Material dto = Objects.requireNonNull(service.findById(materialId));
			
			model.addAttribute("dto", dto);
			model.addAttribute("page", page);
			model.addAttribute("query", query);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
			return "admin/material/materialDetail";
			
		} catch (NullPointerException e) {
			log.debug("materialDetail: ", e);
		} catch (Exception e) {
			log.debug("materialDetail: ", e);
		}
		
		return "redirect:/admin/material/materialList?" + query;		
	}
	
	
}
