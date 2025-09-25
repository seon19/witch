package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Purchase;

public interface PurchaseManageService {
	
	public List<Purchase> listAll();
	public Page<Purchase> listPage(String schType, String kwd, int current_page, int size);

	public void insertPurchase(Purchase entity) throws Exception;
	public void updatePurchase(Purchase entity) throws Exception;
	public void deletePurchase(long purchaseid) throws Exception;
	
	public Purchase findById(long purchaseid);

}
