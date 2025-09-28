package com.sp.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.common.MyUtil;
import com.sp.app.entity.CraftLog;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Member;
import com.sp.app.entity.Potion;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.CraftLogService;
import com.sp.app.service.InventoryService;
import com.sp.app.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/work")
public class WorkroomController {
	private final InventoryService inventoryserive;
	private final CraftLogService craftlogservice;
	private final MemberService memberservice;
	private final MyUtil myUtil;

	@GetMapping("workroom")
	public String workroom(HttpSession session, Model model) throws Exception {
		
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");  
			 model.addAttribute("loginUser", loginUser);
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
	        Page<Inventory> pageInventory = inventoryserive.listPage(loginUser.getMemberId(), schType, kwd, current_page, size);
	       
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
	
	@GetMapping("inventoryDetail/{inventoryId}")
	public String inventoryDetail(@PathVariable("inventoryId") long inventoryId,
			@RequestParam(name = "page") String page,
			@RequestParam(name="schType", defaultValue = "all") String schType,
			Model model) throws Exception {
		
		String query = "page=" + page;
		try {
			Inventory dto = Objects.requireNonNull(inventoryserive.findById(inventoryId));
			
			model.addAttribute("dto", dto);
			model.addAttribute("page", page);
			model.addAttribute("schType", schType);
			
			return "work/inventoryDetail";
		} catch (NullPointerException e) {
			log.debug("inventoryDetail : ", e);
		} catch (Exception e) {
			log.info("inventoryDetail : ", e );
		}
		return "redirect:/work/inventoryList?" + query;
	}
	
	@GetMapping("materials")
	@ResponseBody
	public List<Inventory> getMaterials(HttpSession session) {
	    Long memberId = ((SessionInfo) session.getAttribute("loginUser")).getMemberId();
	    return inventoryserive.listAll(memberId)
	            .stream()
	            .filter(inv -> inv.getMaterial() != null && inv.getQuantity() > 0)
	            .toList();
	}
	
	@PostMapping("craftPotion")
	@ResponseBody
	public Map<String, Object> makePotion(@RequestParam("firstMaterialId") long firstMaterialId,
	                                      @RequestParam("secondMaterialId") long secondMaterialId,
	                                      HttpSession session) {

	    Map<String, Object> result = new HashMap<>();
	    try {
	        Long memberId = ((SessionInfo) session.getAttribute("loginUser")).getMemberId();
	        Potion potion = inventoryserive.craftPotion(memberId, firstMaterialId, secondMaterialId);

	        // 경험치 증가
	        Member updatedMember = memberservice.addExp(memberId, potion.getExp());

	        result.put("success", true);
	        result.put("msg", "성공: " + potion.getPotionName() + " 제조 완료!");
	        result.put("currentExp", updatedMember.getCurrentExp());
	        result.put("currentLevel", updatedMember.getCurrentLevel());

	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("msg", "레시피가 존재하지 않습니다.");
	    }
	    return result;
	}
	
	@GetMapping("/craftLog")
	public String craftLogList(
	        @RequestParam(name = "page", defaultValue = "1") int current_page,
	        @RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
	        Model model,
	        HttpSession session) throws Exception {

	    try {
	    	SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");  
	        int size = 20; 

	        Page<CraftLog> pageCraftLog = craftlogservice.listPage(loginUser.getMemberId(), schType, kwd, current_page, size);

	        int total_page = pageCraftLog.getTotalPages();
	        long dataCount = pageCraftLog.getTotalElements();

	        List<CraftLog> list = pageCraftLog.getContent();

	        model.addAttribute("loginUser", loginUser);
	        model.addAttribute("list", list);
	        model.addAttribute("page", current_page);
	        model.addAttribute("dataCount", dataCount);
	        model.addAttribute("size", size);
	        model.addAttribute("total_page", total_page);

	    } catch (Exception e) {
	        log.info("craftLogList: ", e);
	    }
	    return "work/craftLog :: craftLogList"; 
	}
	
}
