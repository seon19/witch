package com.sp.app.admin.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.admin.service.CrowEventService;
import com.sp.app.admin.service.MaterialService;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.CrowEvent;
import com.sp.app.entity.Material;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/crow/*")
@RequiredArgsConstructor
@Slf4j
public class CrowManageController {

    private final CrowEventService crowEventService;
    private final MaterialService materialService;
    private final MyUtil myUtil;

    @GetMapping("crowList")
	public String crowList(@RequestParam(name = "page", defaultValue = "1") int current_page,
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
			List<CrowEvent> list = null;
			
			Page<CrowEvent> pageCrow = crowEventService.listPage(schType, kwd, current_page, size);
			
			if(pageCrow.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pageCrow.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pageCrow = crowEventService.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pageCrow.getTotalElements();
				
				list = pageCrow.getContent();
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
			log.info("crowList: ", e);
		}
		
		return "admin/crow/crowList";
	}

    @GetMapping("write")
    public String writeForm(Model model, HttpSession session) {
        
    	try {
    		SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
    		
    		List<Material> materialList = materialService.listAll();

    		model.addAttribute("loginUser", loginUser);
            model.addAttribute("mode", "write");
            model.addAttribute("materialList", materialList);
		} catch (Exception e) {
			log.info("writeForm: ", e);
		}
    	
        return "admin/crow/crowWrite";
    }

    @PostMapping("write")
    public String writeSubmit(CrowEvent dto, RedirectAttributes redirectAttr) {
        try {
            crowEventService.insertEvent(dto);
            redirectAttr.addFlashAttribute("message", "이벤트가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("Crow event insert failed", e);
            redirectAttr.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
            redirectAttr.addFlashAttribute("dto", dto);
        }
        return "redirect:/admin/crow/crowList";
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable("id") long id, 
    		@RequestParam("page") int page, 
    		Model model, HttpSession session) {
        
    	try {
    		SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");
    		
    		CrowEvent dto = crowEventService.findById(id);

    		model.addAttribute("loginUser", loginUser);
            model.addAttribute("dto", dto);
            model.addAttribute("page", page);
		} catch (Exception e) {
			log.error("detail", e);
		}
        return "admin/crow/crowDetail";
    }

    @GetMapping("update/{id}")
    public String updateForm(@PathVariable("id") long id, 
    		@RequestParam("page") int page, 
    		Model model, HttpSession session) {
    	
    	try {
    		SessionInfo loginUser = (SessionInfo) session.getAttribute("loginUser");

    		CrowEvent dto = crowEventService.findById(id);
            List<Material> materialList = materialService.listAll();

            model.addAttribute("loginUser", loginUser);
            model.addAttribute("mode", "update");
            model.addAttribute("dto", dto);
            model.addAttribute("page", page);
            model.addAttribute("materialList", materialList);
    		
		} catch (Exception e) {
			log.error("updateForm: ", e);
		}
        
        return "admin/crow/crowWrite";
    }

    @PostMapping("update")
    public String updateSubmit(CrowEvent dto, @RequestParam("page") int page, RedirectAttributes redirectAttr) {
        try {
            crowEventService.updateEvent(dto);
            redirectAttr.addFlashAttribute("message", "이벤트가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("Crow event update failed", e);
            redirectAttr.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttr.addFlashAttribute("dto", dto);
            redirectAttr.addAttribute("id", dto.getCrowEventId());
            redirectAttr.addAttribute("page", page);
            return "redirect:/admin/crow/update/{id}";
        }
        redirectAttr.addAttribute("page", page);
        return "redirect:/admin/crow/crowList";
    }

    @PostMapping("delete/{id}")
    public String delete(@PathVariable("id") long id, @RequestParam("page") int page, RedirectAttributes redirectAttr) {
        try {
            crowEventService.deleteEvent(id);
            redirectAttr.addFlashAttribute("message", "이벤트가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Crow event delete failed", e);
            redirectAttr.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        redirectAttr.addAttribute("page", page);
        return "redirect:/admin/crow/crowList";
    }
}
