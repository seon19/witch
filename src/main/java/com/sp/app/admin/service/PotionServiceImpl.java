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
	private final FileManager fileManager;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;
	
	@Value("${file.upload-path.potion}")
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
	
	@Transactional
	@Override
	public void insertPotion(Potion entity, MultipartFile potionPhoto) throws Exception {
	    String fullPath = uploadPathRoot + File.separator + potionSubPath;
	    
	    try {
	        File dir = new File(fullPath);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        if (potionPhoto != null && !potionPhoto.isEmpty()) {
	            String extension = potionPhoto.getOriginalFilename()
	                    .substring(potionPhoto.getOriginalFilename().lastIndexOf("."));
	            String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);

	            File dest = new File(fullPath, savedFilename);
	            potionPhoto.transferTo(dest);

	            entity.setPotionPhoto(savedFilename);
	        }

	        potionRepository.save(entity);
	    } catch (Exception e) {
	        log.info("insertPotion : ", e);
	        throw e;
	    }
	}

	@Transactional
	@Override
	public void updatePotion(Potion entity, MultipartFile potionPhoto) throws Exception {
	    String fullPath = uploadPathRoot + File.separator + potionSubPath;

	    try {
	        File dir = new File(fullPath);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        if (potionPhoto != null && !potionPhoto.isEmpty()) {
	            if (entity.getPotionPhoto() != null) {
	                File oldFile = new File(fullPath, entity.getPotionPhoto());
	                if (oldFile.exists()) {
	                    oldFile.delete();
	                }
	            }

	            String extension = potionPhoto.getOriginalFilename()
	                    .substring(potionPhoto.getOriginalFilename().lastIndexOf("."));
	            String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);

	            File dest = new File(fullPath, savedFilename);
	            potionPhoto.transferTo(dest);

	            entity.setPotionPhoto(savedFilename);
	        }

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
	        Potion dto = findById(potionId);

	        if (dto == null) {
	            return; 
	        }

	        String fullPath = uploadPathRoot + File.separator + potionSubPath;

	        if (dto.getPotionPhoto() != null && !dto.getPotionPhoto().isEmpty()) {
	            File file = new File(fullPath, dto.getPotionPhoto());
	            if (file.exists()) {
	                file.delete();
	            }
	        }

	        potionRepository.deleteById(potionId);

	    } catch (Exception e) {
	        log.info("deletePotion : ", e);
	        throw e;
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
