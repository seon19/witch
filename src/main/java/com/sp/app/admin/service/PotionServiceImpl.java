package com.sp.app.admin.service;


import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.sp.app.entity.Potion;
import com.sp.app.repository.PotionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class PotionServiceImpl implements PotionService {
	private final PotionRepository potionRepository;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;
	
	@Value("${file.upload-path.material}")
    private String potionSubPath;
	
	@Override
	public List<Potion> listAll() {
		List<Potion> potionList = potionRepository.findAll();
		return potionList;
	}

	@Override
	public Page<Potion> listPage(String schType, String kwd, int current_page, int size) {
		Page<Potion> potion = null;
		
		try {
			Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "potionId"));
		    
			if(kwd.isBlank()) {
				potion = potionRepository.findAll(pageable);
			} else if(schType.equals("potionName")) {
				potion = potionRepository.findByPotionNameContaining(kwd, pageable);
			} else if(schType.equals("potionLevel")) {
			    try {
			        int level = Integer.parseInt(kwd); 
			        potion = potionRepository.findByPotionLevel(level, pageable);
			    } catch (NumberFormatException e) {
			        potion = Page.empty(pageable);
			    }
			}
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			log.info("listPage", e);
		}
		return potion;
	}

	@Override
	public void insertPotion(Potion entity, MultipartFile potionPhoto) throws Exception {
		try {
			if(potionPhoto != null && ! potionPhoto.isEmpty()) {
				String fullPath = System.getProperty("user.dir") + "/src/main/resources/static/dist//uploads/potion";
				
				File f = new File(fullPath);
				if(!f.exists()) {
					f.mkdirs();
				}
				
				String originalFilename = potionPhoto.getOriginalFilename();
				
				String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				
				String saveFilename = timestamp +  "_" + originalFilename;
				
				String pathname = fullPath + "/" + saveFilename;
				File f2 = new File(pathname);
				potionPhoto.transferTo(f2);
				
				entity.setPotionPhoto(saveFilename);	
			}
			potionRepository.save(entity);
		} catch (Exception e) {
			log.info("insertPotion : ", e);
			throw e;
		}
		
	}

	@Override
	@Transactional
	public void updatePotion(Potion entity, MultipartFile potionPhoto) throws Exception {
		try {
			potionRepository.save(entity);
		} catch (Exception e) {
			log.info("updatePotion : ", e);
			throw e;
		}
		
	}

	@Override
	@Transactional
	public void deletePotion(long potionId) throws Exception {
		try {
			potionRepository.deleteById(potionId);
		} catch (Exception e) {
			log.info("deletePotion : ", e);
		}
		
	}

	@Override
	public Potion findById(long potionId) {
		Potion dto = null;
		
		try {
			Optional<Potion> potion = potionRepository.findById(potionId);
			dto = potion.get();
			
		} catch (NoSuchElementException e) {
		} catch (Exception e) {
			log.info("findById : ", e);
		}
		return dto;
	}

	@Override
	public Potion findByPrev(String schType, String kwd, long potionId) {
		Potion dto = null;
		
		try {
			if(kwd.isBlank()) {
				dto = potionRepository.findByPrev(potionId);
			} else if(schType.equals("potionName")) {
				dto = potionRepository.findByPrevName(potionId, kwd);
			} else if(schType.equals("all")) {
				dto = potionRepository.findByPrevAll(potionId, kwd);
			}
		} catch (Exception e) {
			log.info("findByPrev : ", e);
		}
		return dto;
	}

	@Override
	public Potion findByNext(String schType, String kwd, long potionId) {
		Potion dto = null;
		try {
			if(kwd.isBlank()) {
				dto = potionRepository.findByNext(potionId);
			}else if(schType.equals("name")) {
				dto = potionRepository.findByNextName(potionId, kwd);
			} else if(schType.equals("all")) {
				dto = potionRepository.findByNextAll(potionId, kwd);
			}
		} catch (Exception e) {
			log.info("findByNext : ", e);
		}
		return dto;
	}

}
