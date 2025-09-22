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

import com.sp.app.entity.Inventory;
import com.sp.app.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	
	@Override
	public List<Inventory> listAll() {
		List<Inventory> inventoryList = inventoryRepository.findAll();
		return inventoryList;
	}

	@Override
	public Page<Inventory> listPage(String schType, String kwd, int current_page, int size) {
	    Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "inventoryId"));

	    if ("material".equals(schType)) {
	        return inventoryRepository.findByMaterialIsNotNull(pageable);
	    } else if ("potion".equals(schType)) {
	        return inventoryRepository.findByPotionIsNotNull(pageable);
	    } else {
	        return inventoryRepository.findAll(pageable);
	    }
	}

	@Override
	public Inventory findById(long inventoryId) {
		Inventory dto = null;
		
		try {
			Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
			dto = inventory.get();
			
		} catch (NoSuchElementException e) {	
		} catch (Exception e) {
			log.info("findById : ", e);
		}
		return dto;
	}


	
}
