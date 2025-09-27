package com.sp.app.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Potion;
import com.sp.app.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {
	private final RecipeRepository recipeRepository;
	
	@Override
	public List<Potion> listAll() {
		List<Potion> potionList = recipeRepository.findAll();
		return potionList;
	}

	@Override
	public Page<Potion> listPage(String schType, String kwd, int current_page, int size) {
	    Page<Potion> potion = null;
	    
	    try {
	        Pageable pageable = PageRequest.of(current_page - 1, size, Sort.by(Sort.Direction.DESC, "potionId"));
	        
	        if (kwd.isBlank()) {
	            potion = recipeRepository.findAll(pageable);
	        } else if (schType.equals("potionName")) {
	            potion = recipeRepository.findByPotionNameContaining(kwd, pageable);
	        }
	    } catch (IllegalArgumentException e) {
	        log.warn("잘못된 파라미터: {}", e.getMessage());
	    } catch (Exception e) {
	        log.error("listPage 오류", e);
	    }
	    
	    return potion;
	}

	@Override
	public Potion findById(long potionId) {
		Potion dto = null;
		
		try {
			Optional<Potion> potion = recipeRepository.findById(potionId);
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
				dto = recipeRepository.findByPrev(potionId);
			} else if(schType.equals("potionName")) {
				dto = recipeRepository.findByPrevName(potionId, kwd);
			} else if(schType.equals("all")) {
				dto = recipeRepository.findByPrevAll(potionId, kwd);
			}
		} catch (Exception e) {
			log.info("findByPrev : ", e);
		}
		return dto;
	}

	@Override
	public List<Potion> findOwnedPotions(long memberId) {
		return recipeRepository.findOwnedPotions(memberId);
	}


}
