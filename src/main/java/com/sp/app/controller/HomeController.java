package com.sp.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.common.MyUtil;
import com.sp.app.entity.DailyReward;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.HomeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main/*")
public class HomeController {
	
	private final HomeService service;
	
	@GetMapping("home")
	public String home(HttpSession session, Model model) throws Exception {
		 try {
		        SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
		        model.addAttribute("loginUser", loginUser);

		        if (loginUser != null) {
		            boolean hasReceivedToday = service.hasReceivedTodayReward(loginUser.getMemberId());
		            model.addAttribute("hasReceivedToday", hasReceivedToday);
		        }

		    } catch (Exception e) {
		        log.info("home: ", e);
		    }

		    return "main/home";
	}
	
	@GetMapping("dailyReward")
	@ResponseBody
	public Map<String, Object> getDailyReward(HttpSession session) {
	    Map<String, Object> response = new HashMap<>();
	    SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");

	    if (loginUser == null) {
	        response.put("error", "로그인이 필요합니다.");
	        return response;
	    }

	    try {
	        long memberId = loginUser.getMemberId();
	        boolean hasReceived = service.hasReceivedTodayReward(memberId);

	        if (hasReceived) {
	            // [수정된 부분]
	            // 오늘 받은 보상 정보를 DB에서 조회합니다.
	            DailyReward todayReward = service.getTodayReward(memberId);
	            response.put("alreadyReceived", true);

	            // 조회된 보상 정보가 있다면 JSON에 담아줍니다.
	            if (todayReward != null) {
	                response.put("materialName", todayReward.getMaterial().getMaterialName());
	                response.put("materialImage", todayReward.getMaterial().getMaterialPhoto());
	                response.put("quantity", todayReward.getQuantity());
	            }

	        } else {
	            // 새로 지급하는 경우 (기존 코드와 동일)
	            DailyReward newReward = service.giveDailyReward(memberId);
	            response.put("alreadyReceived", false);
	            response.put("materialName", newReward.getMaterial().getMaterialName());
	            response.put("materialImage", newReward.getMaterial().getMaterialPhoto());
	            response.put("quantity", newReward.getQuantity());
	            response.put("consecutiveDays", 1);
	        }

	    } catch (Exception e) {
	        log.error("일일보상 처리 중 오류", e);
	        response.put("error", "보상 처리 중 오류가 발생했습니다.");
	    }

	    return response;
	}

}
