package com.sp.app.service;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Shop;

public interface UserShopService {

	Page<Shop> listAvailableShopItems(int currentPage, int size);
}
