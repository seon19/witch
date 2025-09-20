package com.sp.app.admin.service;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sp.app.common.FileManager;
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
	private final FileManager fileManager;
	
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

	@Transactional
	@Override
	public void insertMaterial(Material entity, MultipartFile materialPhoto) throws Exception {
		
		// 최종 저장 경로
		String fullPath = uploadPathRoot + File.separator + materialSubPath;
		
		try {
			if(materialPhoto != null && ! materialPhoto.isEmpty()) {
				
				String extension = materialPhoto.getOriginalFilename().substring(materialPhoto.getOriginalFilename().lastIndexOf("."));
				String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);
				
				File dest = new File(fullPath, savedFilename);
				materialPhoto.transferTo(dest);
				
				entity.setMaterialPhoto(savedFilename);	
			}
			
			materialRepository.save(entity);
			
		} catch (Exception e) {
			log.info("insertMaterial: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateMaterial(Material entity) throws Exception {
		try {
			materialRepository.save(entity);
		} catch (Exception e) {
			log.info("updateMaterial: ", e);
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void deleteMaterial(long materialid) throws Exception {
		try {
			Material dto = findById(materialid);
			if(dto == null) {
				return;
			}
			
			String fullPath = uploadPathRoot + File.separator + materialSubPath;
			if(dto.getMaterialPhoto() != null) {
				fileManager.deletePath(fullPath + File.separator + dto.getMaterialPhoto());
			}
			
			materialRepository.deleteById(materialid);
		} catch (Exception e) {
			log.info("deleteMaterial: ", e);
			throw e;
		}
	}

	@Override
	public Material findById(long materialid) {
		Material dto = null;
		
		try {
			Optional<Material> material = materialRepository.findById(materialid);
			dto  = material.get();
			
		} catch (NoSuchElementException  e) {
		} catch (Exception e) {
			log.info("findById", e);
		}
		
		return dto;
	}

	

}
