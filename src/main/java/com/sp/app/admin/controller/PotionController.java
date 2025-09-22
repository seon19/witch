package com.sp.app.admin.controller;


import java.io.File;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.admin.service.MaterialService;
import com.sp.app.admin.service.PotionService;
import com.sp.app.common.FileManager;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Material;
import com.sp.app.entity.Potion;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/potion/*")
public class PotionController {
	private final PotionService service;
	private final FileManager fileManager;
	private final MaterialService materialService;
	private final MyUtil myUtil;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;
	
	@Value("${file.upload-path.potion}")
    private String potionSubPath;
	
	@GetMapping("potionList")
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
			List<Potion> list = null;
			
			Page<Potion> pagePotion = service.listPage(schType, kwd, current_page, size);
			
			if(pagePotion.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pagePotion.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pagePotion = service.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pagePotion.getTotalElements();
				
				list = pagePotion.getContent();
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
		return "admin/potion/potionList";
	}
	
	@GetMapping("potionWrite")
	public String writeForm(Model model) throws Exception{
		model.addAttribute("mode", "write");
		
		List<Material> materialList = materialService.listAll();
		model.addAttribute("materialList", materialList);
		
		return "admin/potion/potionWrite";
	}
	
	@PostMapping("write")
	public String writeSubmit(Potion dto, HttpServletRequest req, @RequestParam("selectFile") MultipartFile selectFile) throws Exception{
		try {
			service.insertPotion(dto, selectFile);
		} catch (Exception e) {
			log.info("writeSubmit : " , e);
		}		
		return "redirect:/admin/potion/potionList";

	}
	
	@GetMapping("potionDetail/{potionId}")
	public String potionDetail(@PathVariable("potionId") long potionId,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {

		String query = "page=" + page;
		try {
			kwd = myUtil.decodeUrl(kwd);
			if (! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}

			Potion dto = Objects.requireNonNull(service.findById(potionId));
			

			// 엔터를 <br>로
			//dto.setTasteDescription(dto.getPotionDescription().replaceAll("\n", "<br>"));
			//dto.setPotionComposition(dto.getPotionComposition().replaceAll("\n", "<br>"));
			//dto.setPotionMemo(dto.getPotionMemo().replaceAll("\n", "<br>"));

			// 이전 글, 다음 글
			Potion prevDto = service.findByPrev(schType, kwd, potionId);
			Potion nextDto = service.findByNext(schType, kwd, potionId);

			model.addAttribute("dto", dto);
			model.addAttribute("prevDto", prevDto);
			model.addAttribute("nextDto", nextDto);

			model.addAttribute("page", page);
			model.addAttribute("query", query);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);

			return "admin/potion/potionDetail";
			
		} catch (NullPointerException e) {
			log.debug("potionDetail : ", e);
		} catch (Exception e) {
			log.info("potionDetail : ", e);
		}
		
		return "redirect:/admin/potion/potionList?" + query;
	}
	
	@GetMapping("update/{potionId}")
	public String updateForm(@PathVariable("potionId") long potionId,
			@RequestParam(name = "page") String page,
			Model model) throws Exception {

		try {
			Potion dto = Objects.requireNonNull(service.findById(potionId));
			
			List<Material> materialList = materialService.listAll();
			model.addAttribute("materialList", materialList);

			model.addAttribute("mode", "update");
			model.addAttribute("page", page);
			model.addAttribute("dto", dto);

			return "admin/potion/potionWrite";
		} catch (NullPointerException e) {
			log.debug("updateForm : ", e);
		} catch (Exception e) {
			log.info("updateForm : ", e);
		}
		
		return "redirect:/admin/potion/potionList?page=" + page;
	}

	@PostMapping("update")
	public String updateSubmit(Potion dto, 
			@RequestParam(name = "page") String page,
			@RequestParam("selectFile") MultipartFile selectFile,
			RedirectAttributes redirectAttr) throws Exception {
		
		String fullPath = uploadPathRoot + File.separator + potionSubPath;

		try {
			Potion dbDto = service.findById(dto.getPotionId());
			if(selectFile != null && !selectFile.isEmpty()) {
				if(dto.getPotionPhoto() != null && !dbDto.getPotionPhoto().isEmpty()) {
					fileManager.deletePath(fullPath + File.separator + dbDto.getPotionPhoto());
				}
				
				// 새 파일 저장하기
				String extension = selectFile.getOriginalFilename().substring(selectFile.getOriginalFilename().lastIndexOf("."));
				String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);
				
				File dest = new File(fullPath, savedFilename);
				selectFile.transferTo(dest);
				
				dbDto.setPotionPhoto(savedFilename);
			}
			
			service.updatePotion(dto, selectFile);
		} catch (Exception e) {
		}

		return "redirect:/admin/potion/potionList?page=" + page;
	}
	
	@PostMapping("delete/{potionId}")
	public String delete(@PathVariable("potionId") long potionId,
	                     @RequestParam(name = "page", defaultValue = "1") String page,
	                     @RequestParam(name = "schType", defaultValue = "all") String schType,
	                     @RequestParam(name = "kwd", defaultValue = "") String kwd) throws Exception {
	    String query = "page=" + page;
	    try {
	        kwd = myUtil.decodeUrl(kwd);
	        if (!kwd.isBlank()) {
	            query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
	        }
	        service.deletePotion(potionId);
	    } catch (Exception e) {
	        log.info("delete : ", e);
	    }

	    return "redirect:/admin/potion/potionList?" + query;
	}
	
	
}
