package com.sp.app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.common.MyUtil;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.InventoryService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/work/*")
public class WorkroomController {
	private final InventoryService inventoryserive;
	private final MyUtil myUtil;

	@GetMapping("workroom")
	public String workroom() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("workroom: ", e);
		}
		
		return "work/workroom";
	}
	
	@GetMapping("inventoryList")
	public String list(
	        @RequestParam(name = "page", defaultValue = "1") int current_page,
	        @RequestParam(name = "schType", defaultValue = "material") String schType,
	        @RequestParam(name = "kwd", defaultValue = "") String kwd,
	        Model model,
	        HttpSession session) throws Exception {

	    try {
	        SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");  
	        kwd = myUtil.decodeUrl(kwd);

	        int size = 10;
	        Page<Inventory> pageInventory = inventoryserive.listPage(schType, kwd, current_page, size);

	        int total_page = pageInventory.getTotalPages();
	        long dataCount = pageInventory.getTotalElements();

	        List<Inventory> list = pageInventory.getContent();

	        model.addAttribute("loginUser", loginUser);
	        model.addAttribute("list", list);
	        model.addAttribute("page", current_page);
	        model.addAttribute("dataCount", dataCount);
	        model.addAttribute("size", size);
	        model.addAttribute("total_page", total_page);
	        model.addAttribute("schType", schType);
	        model.addAttribute("kwd", kwd);

	    } catch (Exception e) {
	        log.info("inventoryList: ", e);
	    }
	    return "work/inventory :: inventoryList";
	}
}
