package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Shop;
import com.sp.app.repository.UserShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPurchaseServiceImpl implements UserShopService {

	private final UserShopRepository shopRepository;

    @Override
    public Page<Shop> listAvailableShopItems(int currentPage, int size) {
        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.DESC, "shopId")); 
        
        return shopRepository.findByIsAvailableTrue(pageable);
    }

}
