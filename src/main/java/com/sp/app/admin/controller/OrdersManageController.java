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
import com.sp.app.admin.service.PotionManageService;
import com.sp.app.admin.service.PotionService;
import com.sp.app.admin.service.PurchaseManageService;
import com.sp.app.admin.service.ShopManageService;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Material;
import com.sp.app.entity.Potion;
import com.sp.app.entity.Purchase;
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

	private final PurchaseManageService pService; // 구매
	private final ShopManageService sService; // 판매
	private final PotionService poService; // 판매
	private final PotionManageService pomService;
	private final MaterialService mService;
	private final MyUtil myUtil;
	
	
	// AJAX로 재료 정보 가져오기
	// 재료id로 정보를 JSON 형태로 반환한다
	@GetMapping("/api/material/{materialId}")
	@ResponseBody // @RestController로 별도의 컨트롤러를 만들면ㄷ @ResponseBody를 생략할 수 있다
	public Material getMaterialInfo(@PathVariable("materialId") long materialId) {
		return mService.findById(materialId);
	}
	
	@GetMapping("/api/potion/{potionId}")
	@ResponseBody
	public Potion getPotionInfo(@PathVariable("potionId") long potionId) {
		return pomService.findById(potionId);
	}
	
	/* 구매 */
	@GetMapping("purchaseList")
	public String purchaseList(@RequestParam(name = "page", defaultValue = "1") int current_page,
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
			List<Purchase> list = null;
			
			Page<Purchase> pagePurchase = pService.listPage(schType, kwd, current_page, size);
			
			if(pagePurchase.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pagePurchase.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pagePurchase = pService.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pagePurchase.getTotalElements();
				
				list = pagePurchase.getContent();
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
			log.info("purchaseList: ", e);
		}
		
		return "admin/orders/purchaseList";
	}
	
	@GetMapping("purchaseWrite")
	public String purchaseWriteForm(Model model, HttpSession session) throws Exception {
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			// AJAX로 selectBox를 채우려면 모든 재료 목록이 필요
			List<Material> materialList = mService.listAll();
			List<Potion> potionList = poService.listAll();
			
			// 이미 등록된 Purchase 상품 목록 가져오기
			List<Purchase> existingPurchaseItems = pService.listAll();
			Set<Long> registeredMaterialIds = new HashSet<>();
		    Set<Long> registeredPotionIds = new HashSet<>();

		    for (Purchase item : existingPurchaseItems) {
		    	if (item.getMaterial() != null) {
		            registeredMaterialIds.add(item.getMaterial().getMaterialId());
		        } else if (item.getPotion() != null) {
		            registeredPotionIds.add(item.getPotion().getPotionId());
		        }
	        }
			
		    model.addAttribute("loginUser", loginUser);
			model.addAttribute("materialList", materialList);
			model.addAttribute("potionList", potionList);
			model.addAttribute("registeredMaterialIds", registeredMaterialIds);
			model.addAttribute("registeredPotionIds", registeredPotionIds);
			model.addAttribute("mode", "purchaseWrite");
			
		} catch (Exception e) {
			log.info("purchaseWriteForm: ", e);
		}
		
		return "admin/orders/purchaseWrite";
	}
	
	@PostMapping("purchaseWrite")
	public String purchaseWriteSubmit(Purchase dto, 
			@RequestParam("itemType") String itemType,
	        @RequestParam("itemId") long itemId,
			HttpServletRequest req) throws Exception {
		
		try {
			if ("material".equalsIgnoreCase(itemType)) {
	            Material material = mService.findById(itemId);
	            if(material == null) throw new RuntimeException("Material not found");
	            dto.setMaterial(material);

	        } else if ("potion".equalsIgnoreCase(itemType)) {
	            Potion potion = pomService.findById(itemId); 
	            if(potion == null) throw new RuntimeException("Potion not found");
	            dto.setPotion(potion);
	        
	        } else {
	            throw new IllegalArgumentException("Invalid item type: " + itemType);
	        }

	        pService.insertPurchase(dto);
			
		} catch (Exception e) {
			log.info("purchaseWriteSubmit: ", e);
		}
		
		return "redirect:/admin/orders/purchaseList";
	}
	
	@GetMapping("purchaseDetail/{purchaseId}")
	public String purchaseDetail(@PathVariable("purchaseId") long purchaseId, @RequestParam(name = "page") int page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model, HttpSession session) throws Exception {
		
		String query = "page=" + page;
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			Purchase dto = Objects.requireNonNull(pService.findById(purchaseId));
			
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("dto", dto);
			model.addAttribute("page", page);
			model.addAttribute("query", query);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
			return "admin/orders/purchaseDetail";
			
		} catch (NullPointerException e) {
			log.debug("purchaseDetail: ", e);
		} catch (Exception e) {
			log.debug("purchaseetail: ", e);
		}
		
		return "redirect:/admin/orders/purchaseList?" + query;		
	}
	
	@GetMapping("purchaseUpdate/{purchaseId}")
	public String updatePurchaseForm(@PathVariable("purchaseId") long purchaseId,
			@RequestParam(name = "page") String page,
			Model model, HttpSession session) throws Exception {

		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			Purchase dto = pService.findById(purchaseId);
	        			
			List<Purchase> existingPurchaseItems = pService.listAll();
	        
	        List<Material> materialList = mService.listAll();
	        List<Potion> potionList = poService.listAll();
	        
	        Set<Long> registeredMaterialIds = new HashSet<>();
	        Set<Long> registeredPotionIds = new HashSet<>();
		    
	        for (Purchase item : existingPurchaseItems) {
	            if (item.getMaterial() != null) {
	                registeredMaterialIds.add(item.getMaterial().getMaterialId());
	            } else if (item.getPotion() != null) {
	                registeredPotionIds.add(item.getPotion().getPotionId());
	            }
	        }

	        model.addAttribute("loginUser", loginUser);
		    model.addAttribute("materialList", materialList);
	        model.addAttribute("potionList", potionList);
	        model.addAttribute("registeredMaterialIds", registeredMaterialIds);
	        model.addAttribute("registeredPotionIds", registeredPotionIds);

	        model.addAttribute("mode", "purchaseUpdate");
	        model.addAttribute("page", page);
	        model.addAttribute("dto", dto);

			return "admin/orders/purchaseWrite";
		} catch (NullPointerException e) {
			log.debug("updatePurchaseForm : ", e);
		} catch (Exception e) {
			log.info("updatePurchaseForm : ", e);
		}
		
		return "redirect:/admin/orders/purchaseList?page=" + page;
	}

	@PostMapping("purchaseUpdate")
	public String updatePurchaseSubmit(Purchase formDto, 
			@RequestParam(name = "page") String page) throws Exception {

		try {
			// 원래 데이터 가져오기
			Purchase dbDto = pService.findById(formDto.getPurchaseId());
			
			dbDto.setPurchasePrice(formDto.getPurchasePrice());
	        dbDto.setIsAvailable(formDto.getIsAvailable());
	        			
			pService.updatePurchase(dbDto);
		} catch (Exception e) {
		}

		return "redirect:/admin/orders/purchaseList?page=" + page;
	}
	
	@PostMapping("/purchaseDelete/{purchaseId}")
	public String purchaseDelete(@PathVariable("purchaseId") long purchaseId,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd) {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			pService.deletePurchase(purchaseId);
		} catch (Exception e) {
			log.info("purchaseDelete", e);
		}
		
		return "redirect:/admin/orders/purchaseList?" + query;
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
	public String saleWriteForm(Model model, HttpSession session) throws Exception {
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			// AJAX로 selectBox를 채우려면 모든 재료 목록이 필요
			List<Material> materialList = mService.listAll();
			
			// 이미 등록된 Shop 상품 목록 가져오기
			List<Shop> existingShopItems = sService.listAll();
			
			Set<Long> registeredMaterialIds = new HashSet<>();

		    for (Shop item : existingShopItems) {
		        registeredMaterialIds.add(item.getMaterial().getMaterialId());
		    }
			
		    model.addAttribute("loginUser", loginUser);
			model.addAttribute("materialList", materialList);
			model.addAttribute("registeredMaterialIds", registeredMaterialIds);
			model.addAttribute("mode", "saleWrite");
			
		} catch (Exception e) {
			log.info("saleWriteForm: ", e);
		}
		
		
		return "admin/orders/saleWrite";
	}
	
	@PostMapping("saleWrite")
	public String saleWriteSubmit(Shop dto, 
			@RequestParam(name = "materialId") long materialId, 
			HttpServletRequest req, HttpSession session, Model model) throws Exception {
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			Material material = mService.findById(materialId);
			dto.setMaterial(material);
			
			model.addAttribute("loginUser", loginUser);
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
			Model model, HttpSession session) throws Exception {
		
		String query = "page=" + page;
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			Shop dto = Objects.requireNonNull(sService.findById(shopId));
			
			model.addAttribute("loginUser", loginUser);
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
			Model model, HttpSession session) throws Exception {

		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
			
			Shop dto = sService.findById(shopId);
			
			List<Material> materialList = mService.listAll();
		    List<Shop> existingShopItems = sService.listAll();
		    Set<Long> registeredMaterialIds = new HashSet<>();
		    for (Shop item : existingShopItems) {
		        registeredMaterialIds.add(item.getMaterial().getMaterialId());
		    }

		    model.addAttribute("loginUser", loginUser);
		    model.addAttribute("materialList", materialList);
		    model.addAttribute("registeredMaterialIds", registeredMaterialIds);

			model.addAttribute("mode", "saleUpdate");
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
	public String updateSubmit(Shop formDto, 
			@RequestParam(name = "page") String page) throws Exception {

		try {
			// 원래 데이터 가져오기
			Shop dbDto = sService.findById(formDto.getShopId());
			
			dbDto.setSellingPrice(formDto.getSellingPrice());
	        dbDto.setIsAvailable(formDto.getIsAvailable());
	        			
			sService.updateShop(dbDto);
		} catch (Exception e) {
		}

		return "redirect:/admin/orders/saleList?page=" + page;
	}
	
	@PostMapping("/saleDelete/{shopId}")
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
		
		return "redirect:/admin/orders/saleList?" + query;
	}
}
