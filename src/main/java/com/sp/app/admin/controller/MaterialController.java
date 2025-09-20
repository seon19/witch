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
import com.sp.app.common.FileManager;
import com.sp.app.common.MyUtil;
import com.sp.app.entity.Material;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/material/*")
public class MaterialController {

	private final MaterialService service;
	private final FileManager fileManager;
	private final MyUtil myUtil;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;
	
	@Value("${file.upload-path.material}")
    private String materialSubPath;
	
	@GetMapping("materialList")
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
			List<Material> list = null;
			
			Page<Material> pageMaterial = service.listPage(schType, kwd, current_page, size);
			
			if(pageMaterial.isEmpty()) {
				current_page = 0;
			} else {
				total_page = pageMaterial.getTotalPages();
				
				if(current_page > total_page && total_page > 0) {
					current_page = total_page;
					pageMaterial = service.listPage(schType, kwd, current_page, size);
				}
				
				dataCount = pageMaterial.getTotalElements();
				
				list = pageMaterial.getContent();
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
		
		return "admin/material/materialList";
	}
	
	@GetMapping("write")
	public String writeForm(Model model) throws Exception {
		
		model.addAttribute("mode", "write");
		
		return "admin/material/materialWrite";
	}
	
	@PostMapping("write")
	public String writeSubmit(Material dto, HttpServletRequest req, @RequestParam("selectFile") MultipartFile selectFile) throws Exception {
		
		try {
			service.insertMaterial(dto, selectFile);
			
		} catch (Exception e) {
			log.info("writeSubmit: ", e);
		}
		
		return "redirect:/admin/material/materialList";
	}
	
	@GetMapping("materialDetail/{materialId}")
	public String materialDetail(@PathVariable("materialId") long materialId, @RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			Material dto = Objects.requireNonNull(service.findById(materialId));
			
			model.addAttribute("dto", dto);
			model.addAttribute("page", page);
			model.addAttribute("query", query);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
			return "admin/material/materialDetail";
			
		} catch (NullPointerException e) {
			log.debug("materialDetail: ", e);
		} catch (Exception e) {
			log.debug("materialDetail: ", e);
		}
		
		return "redirect:/admin/material/materialList?" + query;		
	}
	
	@GetMapping("update/{materialId}")
	public String materialUpdateForm(@PathVariable("materialId") long materialId,
			@RequestParam(name = "page") String page,
			Model model) throws Exception {

		try {
			Material dto = Objects.requireNonNull(service.findById(materialId));

		
			
			model.addAttribute("mode", "update");
			model.addAttribute("page", page);
			model.addAttribute("dto", dto);

			return "admin/material/materialWrite";
		} catch (NullPointerException e) {
			log.debug("materialUpdateForm : ", e);
		} catch (Exception e) {
			log.info("materialUpdateForm : ", e);
		}
		
		return "redirect:/admin/material/materialList?page=" + page;
	}
	
	@PostMapping("update")
	public String materialUpdateSubmit(Material formDto, @RequestParam(name = "page") int page,
			@RequestParam(name = "selectFile") MultipartFile selectFile,
			RedirectAttributes redirectAttr) {
		
		String fullPath = uploadPathRoot + File.separator + materialSubPath;
		
		try {
			// 원래 데이터 가져오기
			Material dbDto = service.findById(formDto.getMaterialId());
			if(dbDto == null) {
				redirectAttr.addAttribute("page", page);
				return "redirect:/admin/material/materialList";
			}
			
			// 새 파일이 업로드 되었는지 확인하기
			if(selectFile != null && !selectFile.isEmpty()) {
				if(dbDto.getMaterialPhoto() != null && !dbDto.getMaterialPhoto().isEmpty()) {
					fileManager.deletePath(fullPath + File.separator + dbDto.getMaterialPhoto());
				}
				
				// 새 파일 저장하기
				String extension = selectFile.getOriginalFilename().substring(selectFile.getOriginalFilename().lastIndexOf("."));
				String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);
				
				File dest = new File(fullPath, savedFilename);
				selectFile.transferTo(dest);
				
				dbDto.setMaterialPhoto(savedFilename);
			}
			dbDto.setMaterialName(formDto.getMaterialName());
			dbDto.setMaterialDescription(formDto.getMaterialDescription());
			dbDto.setMaterialPrice(formDto.getMaterialPrice());
			dbDto.setMaterialLevel(formDto.getMaterialLevel());
			dbDto.setMaterialEffect(formDto.getMaterialEffect());
			
			service.updateMaterial(dbDto);
		} catch (Exception e) {
			log.info("updateSubmit: ", e);
		}
		
		redirectAttr.addAttribute("page", page);
		return "redirect:/admin/material/materialList";
	}
	
	@PostMapping("delete/{materialId}")
	public String materialDelete(@PathVariable("materialId") long materialId,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd) {
		
		String query = "page=" + page;
		
		try {
			kwd = myUtil.decodeUrl(kwd);
			
			if(! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			service.deleteMaterial(materialId);
		} catch (Exception e) {
			log.info("materialDelete", e);
		}
		
		return "redirect:/admin/material/materialList?" + query;
	}
	
	
}
