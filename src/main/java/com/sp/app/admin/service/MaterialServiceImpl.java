package com.sp.app.admin.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sp.app.entity.Material;
import com.sp.app.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements MaterialService {
	
	private final MaterialRepository materialRepository;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;
	
	@Value("${file.upload-path.material}")
    private String materialSubPath;
	
	@Override
	public List<Material> listAll() {
		List<Material> list = materialRepository.findAll();
		return list;
	}

	@Override
	public Page<Material> listPage(String schType, String kwd, int current_page, int size) {
		Page<Material> p = null;
		
		try {
			Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "materialId"));
			
			if(kwd.isBlank()) {
				p = materialRepository.findAll(pageable);
			} else if(schType.equals("materialName")) {
				p = materialRepository.findByMaterialNameContaining(kwd, pageable);
			}
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			log.info("listPage", e);
		}
		return p;
	}

	@Override
	public void insertMaterial(Material entity, MultipartFile materialPhoto) throws Exception {

		try {
			if(materialPhoto != null && ! materialPhoto.isEmpty()) {
				
				// 최종 저장 경로
				String fullPath = uploadPathRoot + File.separator + materialSubPath;
				
				// 폴더 없으면 폴더 만들기
				File f = new File(fullPath);
				if(!f.exists()) {
					f.mkdirs();
				}
				
				String originalFilename = materialPhoto.getOriginalFilename();
				
				String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				
				String saveFilename = timestamp +  "_" + originalFilename;
				
				String pathname = fullPath + File.separator + saveFilename;
				File f2 = new File(pathname);
				materialPhoto.transferTo(f2);
				
				entity.setMaterialPhoto(saveFilename);	
			}
			
			materialRepository.save(entity);
			
		} catch (Exception e) {
			log.info("insertMaterial: ", e);
			throw e;
		}
	}

	@Override
	public void updateMaterial(Material entity) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteMaterial(long materialid) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Material findById(long materialid) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
