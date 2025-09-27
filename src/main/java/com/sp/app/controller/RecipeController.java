package com.sp.app.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.app.common.MyUtil;
import com.sp.app.entity.Potion;
import com.sp.app.entity.SessionInfo;
import com.sp.app.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recipe/*")
public class RecipeController {
	private final RecipeService service;
	private final MyUtil myUtil;
	@GetMapping("recipeMain")
	public String list(@RequestParam(name = "page", defaultValue = "1") int current_page,
	                   @RequestParam(name = "schType", defaultValue = "all") String schType,
	                   @RequestParam(name = "kwd", defaultValue = "") String kwd,
	                   Model model,
	                   HttpSession session) throws Exception {
	    try {
	        SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
	        if (loginUser == null) {
	            return "redirect:/member/login";
	        }

	        long memberId = loginUser.getMemberId();
	        kwd = myUtil.decodeUrl(kwd);

	        int total_page = 0;
	        int size = 2;
	        long dataCount = 0;
	        List<Potion> list = null;

	        Page<Potion> pagePotion = service.listPage(schType, kwd, current_page, size);

	        if (pagePotion.isEmpty()) {
	            current_page = 0;
	        } else {
	            total_page = pagePotion.getTotalPages();

	            if (current_page > total_page && total_page > 0) {
	                current_page = total_page;
	                pagePotion = service.listPage(schType, kwd, current_page, size);
	            }

	            dataCount = pagePotion.getTotalElements();
	            list = pagePotion.getContent();
	        }

	        List<Potion> ownedPotions = service.findOwnedPotions(memberId);
	        Set<Long> ownedPotionIds = ownedPotions.stream()
	                .map(Potion::getPotionId)
	                .collect(Collectors.toSet());

	        for (Potion p : list) {
	            boolean owned = ownedPotionIds.contains(p.getPotionId());

	            if (!owned) {
	                p.setPotionName("???");
	                p.setPotionDescription("???");
	                p.setTasteDescription("???");
	                p.setPotionComposition("???");
	                p.setPotionMemo("???");
	                p.setPotionPhoto("unknown.png");
	            }
	        }

	        model.addAttribute("loginUser", loginUser);
	        model.addAttribute("list", list);
	        model.addAttribute("page", current_page);
	        model.addAttribute("dataCount", dataCount);
	        model.addAttribute("size", size);
	        model.addAttribute("total_page", total_page);
	        model.addAttribute("schType", schType);
	        model.addAttribute("kwd", kwd);
	        model.addAttribute("ownedPotionIds", ownedPotionIds);

	    } catch (Exception e) {
	        log.error("recipeMain 오류: ", e);
	    }
	    return "recipe/recipeMain";
	}
}