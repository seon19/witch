package com.sp.app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Inventory;
import com.sp.app.entity.Potion;


public interface InventoryService {
	public List<Inventory> listAll(long memberId);
	
	public Page<Inventory> listPage(long memberId, String schType, String kwd, int current_page, int size);
	
	public Inventory findById(long inventoryId);

	public Potion craftPotion(long memberId, long firstMaterialId, long secondMaterialId) throws Exception;
}
