package com.sp.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.dto.UserPurchaseRequestDTO;
import com.sp.app.entity.Member;
import com.sp.app.entity.SessionInfo;
import com.sp.app.entity.Shop;
import com.sp.app.service.MemberService;
import com.sp.app.service.OrderService;
import com.sp.app.service.UserShopService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders/*")
public class OrderController {
	
	private final UserShopService userShopService;
	private final OrderService orderService;
	private final MemberService memberService;
	
	/* 구매 페이지 */
	@GetMapping("purchaseList")
	public String purchaseList(@RequestParam(name = "page", defaultValue = "1") int page, Model model, HttpSession session) {
        
		try {
			SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
	        
			int size = 10;
	       
	        Page<Shop> pageData = userShopService.listAvailableShopItems(page, size);
	        
	        model.addAttribute("loginUser", loginUser);
	        model.addAttribute("list", pageData.getContent());
	        model.addAttribute("page", page);
	        model.addAttribute("dataCount", pageData.getTotalElements());
	        model.addAttribute("total_page", pageData.getTotalPages());
		} catch (Exception e) {
			log.info("purchaseList: ", e);
		}
		
        return "/orders/purchaseList";
    }
	
	/* 사용자 구매 */
	@PostMapping("/purchase")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> purchaseItem(@RequestBody UserPurchaseRequestDTO purchaseRequest, HttpSession session) {
	    Map<String, Object> response = new HashMap<>();
	    SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");

	    if (loginUser == null) {
	        response.put("success", false);
	        response.put("message", "로그인이 필요합니다.");
	        return ResponseEntity.status(401).body(response);
	    }

	    try {
	        orderService.purchaseItem(
	                loginUser.getMemberId(),
	                purchaseRequest.getShopId(),
	                purchaseRequest.getQuantity()
	        );

	        Member updatedMember = memberService.findById(loginUser.getMemberId()); 
	        
	        loginUser.setCurrentBalance(updatedMember.getCurrentBalance());
	        session.setAttribute("loginUser", loginUser);

	        response.put("success", true);
	        response.put("message", "구매가 완료되었습니다.");
	        response.put("currentBalance", updatedMember.getCurrentBalance()); 
	        return ResponseEntity.ok(response);

	    } catch (IllegalStateException e) {
	        log.warn("구매 실패: {}", e.getMessage());
	        response.put("success", false);
	        response.put("message", e.getMessage());
	        return ResponseEntity.badRequest().body(response);
	    } catch (Exception e) {
	        log.error("구매 처리 중 서버 오류", e);
	        response.put("success", false);
	        response.put("message", "서버 오류로 인해 구매에 실패했습니다.");
	        return ResponseEntity.internalServerError().body(response);
	    }
	}

	
	
	
	/* 판매 */
	@GetMapping("saleList")
	public String saleList() throws Exception {
		
		try {
			
			
		} catch (Exception e) {
			log.info("saleList: ", e);
		}
		
		return "orders/saleList";
	}

}
