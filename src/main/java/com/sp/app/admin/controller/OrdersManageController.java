package com.sp.app.admin.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.admin.service.MaterialService;
import com.sp.app.admin.service.OrdersManageService;
import com.sp.app.admin.service.ShopManageService;
import com.sp.app.common.FileManager;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Material;
import com.sp.app.entity.Orders;
import com.sp.app.entity.SessionInfo;
import com.sp.app.entity.Shop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/orders/*")
public class OrdersManageController {

	private final OrdersManageService service;
	private final ShopManageService sService;
	private final MaterialService mService;
	private final MyUtil myUtil;
	
	@GetMapping("purchaseList")
	public String list(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model,
			HttpSession session) throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("purchaseList: ", e);
		}
		
		return "admin/orders/purchaseList";
	}
	
	@GetMapping("write")
	public String writeForm(Model model) throws Exception {
		
		model.addAttribute("mode", "write");
		
		return "admin/orders/ordersWrite";
	}
	
	@PostMapping("write")
	public String writeSubmit(Orders dto, HttpServletRequest req) throws Exception {
		
		try {
			service.insertOrders(dto);
			
		} catch (Exception e) {
			log.info("writeSubmit: ", e);
		}
		
		return "redirect:/admin/orders/purchaseList";
	}
	
	
	
	
	/* 판매 */
	@GetMapping("saleList")
	public String saleList(@RequestParam(name = "page", defaultValue = "1") int current_page,
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
			List<Shop> list = null;
			
			Page<Shop> pageShop = sService.listPage(schType, kwd, current_page, size);
			
			if(pageShop.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pageShop.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pageShop = sService.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pageShop.getTotalElements();
				
				list = pageShop.getContent();
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
			log.info("saleList: ", e);
		}
		
		return "admin/orders/saleList";
	}
	
	@GetMapping("saleWrite")
	public String saleWriteForm(Model model) throws Exception {
		
		// AJAX로 selectBox를 채우려면 모든 재료 목록이 필요
		List<Material> materialList = mService.listAll();
		
		// 이미 등록된 Shop 상품 목록 가져오기
		List<Shop> existingShopItems = sService.listAll();
		
		Set<Long> registeredMaterialIds = new HashSet<>();

	    for (Shop item : existingShopItems) {
	        registeredMaterialIds.add(item.getMaterial().getMaterialId());
	    }
		
		model.addAttribute("materialList", materialList);
		model.addAttribute("registeredMaterialIds", registeredMaterialIds);
		model.addAttribute("mode", "saleWrite");
		
		return "admin/orders/saleWrite";
	}
	
	// AJAX로 재료 정보 가져오기
	// 재료id로 정보를 JSON 형태로 반환한다
	@GetMapping("/api/material/{materialId}")
	@ResponseBody // @RestController로 별도의 컨트롤러를 만들면ㄷ @ResponseBody를 생략할 수 있다
	public Material getMaterialInfo(@PathVariable("materialId") long materialId) {
		return mService.findById(materialId);
	}
	
	@PostMapping("saleWrite")
	public String saleWriteSubmit(Shop dto, @RequestParam(name = "materialId") long materialId, HttpServletRequest req) throws Exception {
		
		try {
			Material material = mService.findById(materialId);
			dto.setMaterial(material);
			
			sService.insertShop(dto);
			
		} catch (Exception e) {
			log.info("saleWriteSubmit: ", e);
		}
		
		return "redirect:/admin/orders/saleList";
	}
	
	@GetMapping("saleDetail/{shopId}")
	public String saleDetail(@PathVariable("shopId") long shopId, @RequestParam(name = "page") int page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			Shop dto = Objects.requireNonNull(sService.findById(shopId));
			
			model.addAttribute("dto", dto);
			model.addAttribute("page", page);
			model.addAttribute("query", query);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
			return "admin/orders/saleDetail";
			
		} catch (NullPointerException e) {
			log.debug("saleDetail: ", e);
		} catch (Exception e) {
			log.debug("saleDetail: ", e);
		}
		
		return "redirect:/admin/orders/saleList?" + query;		
	}
	
	@GetMapping("saleUpdate/{shopId}")
	public String updateSaleForm(@PathVariable("shopId") long shopId,
			@RequestParam(name = "page") String page,
			Model model) throws Exception {

		try {
			Shop dto = Objects.requireNonNull(sService.findById(shopId));

			model.addAttribute("mode", "updateSale");
			model.addAttribute("page", page);
			model.addAttribute("dto", dto);

			return "admin/orders/saleWrite";
		} catch (NullPointerException e) {
			log.debug("updateSaleForm : ", e);
		} catch (Exception e) {
			log.info("updateSaleForm : ", e);
		}
		
		return "redirect:/admin/orders/saleList?page=" + page;
	}

	@PostMapping("saleUpdate")
	public String updateSubmit(Shop dto, 
			@RequestParam(name = "page") String page) throws Exception {

		try {
			sService.updateShop(dto);
		} catch (Exception e) {
		}

		return "redirect:/admin/orders/saleList?page=" + page;
	}
	
	@PostMapping("saleDelete/{shopId}")
	public String shopDelete(@PathVariable("shopId") long shopId,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd) {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			sService.deleteShop(shopId);
		} catch (Exception e) {
			log.info("shopDelete", e);
		}
		
		return "redirect:/admin/orders/shopList?" + query;
	}
}
