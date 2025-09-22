package com.sp.app.admin.service;

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

import com.sp.app.entity.Shop;
import com.sp.app.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopManageServiceImpl implements ShopManageService {

	// 판매 품목 서비스
	
	private final ShopRepository shopRepository;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;

	@Override
	public List<Shop> listAll() {
		List<Shop> list = shopRepository.findAll();
		return list;
	}

	@Override
	public Page<Shop> listPage(String schType, String kwd, int current_page, int size) {
		Page<Shop> p = null;
		
		try {
			Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "shopId"));
			
			if(kwd.isBlank()) {
				p = shopRepository.findAll(pageable);
			} else if(schType.equals("materialName")) {
				p = shopRepository.findByMaterial_MaterialNameContaining(kwd, pageable);
			}
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			log.info("listPage", e);
		}
		return p;
	}

	@Transactional
	@Override
	public void insertShop(Shop entity) throws Exception {
		try {
			shopRepository.save(entity);
		} catch (Exception e) {
			log.info("insertShop: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateShop(Shop entity) throws Exception {
		try {
			shopRepository.save(entity);
		} catch (Exception e) {
			log.info("updateShop: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void deleteShop(long shopid) throws Exception {
		try {
			shopRepository.deleteById(shopid);
		} catch (Exception e) {
			log.info("deleteShop: ", e);
			throw e;
		}
	}

	@Override
	public Shop findById(long shopid) {
		
		try {
			return shopRepository.findById(shopid).orElse(null);
		
		} catch (Exception e) {
			log.info("findById: ", e);
			return null;
		}
		
	}

}
