	package com.sp.app.controller;
	
	import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.entity.Member;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
	
	@Controller
	@RequiredArgsConstructor
	@Slf4j
	@RequestMapping(value = "/member/*")
	public class MemberController {
		
		private final MemberService service;
		
		@GetMapping("login")
		public String loginForm(Model model) {
		    return "member/login";
		}
		
		@PostMapping("login")
		public String loginSubmit(
			    @RequestParam(name = "userId") String userId,
			    @RequestParam(name = "password") String password,
			    HttpSession session,
			    Model model) {
	
		    try {
		        Map<String, Object> map = new HashMap<>();
		        map.put("userId", userId);
		        map.put("password", password);
	
		        Member member = service.loginMember(map);
	
		        if (member == null) {
		        	model.addAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다");
		            return "member/login";
		        }
		        
		        SessionInfo info = SessionInfo.builder()
		        		.memberId(member.getMemberId())
		        		.userId(member.getUserId())
		        		.name(member.getName())
		        		.nickname(member.getNickname())
		        		.email(member.getEmail())
		        		.currentLevel(member.getCurrentLevel())
		        		.currentExp(member.getCurrentExp())
		        		.currentBalance(member.getCurrentBalance())
		        		.profilePhoto(member.getProfilePhoto())
		        		.build();
		        
		        session.setMaxInactiveInterval(30 * 60);
		        
		        session.setAttribute("loginUser", info);
	
		        if (member.getRole() == 1) {
		            return "redirect:/main/home";
		        } else if (member.getRole() == 99) {
		            return "redirect:/admin/main";
		        } else {
		            return "redirect:/main/home";
		        }
	
		    } catch (Exception e) {
		    	log.info("login : ", e);
		        return "member/login";
		    }
		}
		
	
	    @GetMapping("/main/home")
	    public String mainHome() {
	        return "main/home";
	    }
	
	    @GetMapping("/admin/main")
	    public String adminMain() {
	        return "admin/main";
	    }
		
	    
	    @GetMapping("logout")
		public String logout(HttpSession session) {

			session.removeAttribute("loginUser");

			session.invalidate();

			return "redirect:/member/login";
		}
	
	}
