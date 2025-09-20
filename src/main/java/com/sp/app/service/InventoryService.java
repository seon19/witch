package com.sp.app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Inventory;

public interface InventoryService {
	public List<Inventory> listAll();
	public Page<Inventory> listPage(String schType, String kwd, int current_page, int size);
	
	public Inventory findById(long inventoryId);

}
