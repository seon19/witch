package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Shop;

public interface ShopManageService {
	
	public List<Shop> listAll();
	public Page<Shop> listPage(String schType, String kwd, int current_page, int size);

	public void insertShop(Shop entity) throws Exception;
	public void updateShop(Shop entity) throws Exception;
	public void deleteShop(long orderid) throws Exception;
	
	public Shop findById(long orderid);

}
