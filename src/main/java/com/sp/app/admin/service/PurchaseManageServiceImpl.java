package com.sp.app.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Purchase;
import com.sp.app.repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseManageServiceImpl implements PurchaseManageService {

	// 구매 품목 서비스
	
	private final PurchaseRepository purchaseRepository;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;

	@Override
	public List<Purchase> listAll() {
		List<Purchase> list = purchaseRepository.findAllWithDetails();
		return list;
	}

	@Override
	public Page<Purchase> listPage(String schType, String kwd, int current_page, int size) {
		Page<Purchase> p = null;
		
		try {
			Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
			
			if(kwd.isBlank()) {
				p = purchaseRepository.findAll(pageable);
			} else if(schType.equals("materialName")) {
				p = purchaseRepository.findByMaterial_MaterialNameContaining(kwd, pageable);
			}
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			log.info("listPage", e);
		}
		return p;
	}

	@Transactional
	@Override
	public void insertPurchase(Purchase entity) throws Exception {
		try {
			purchaseRepository.save(entity);
		} catch (Exception e) {
			log.info("insertPurchase: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updatePurchase(Purchase entity) throws Exception {
		try {
			purchaseRepository.save(entity);
		} catch (Exception e) {
			log.info("updatePurchase: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void deletePurchase(long Purchaseid) throws Exception {
		try {
			purchaseRepository.deleteById(Purchaseid);
		} catch (Exception e) {
			log.info("deletePurchase: ", e);
			throw e;
		}
	}

	@Override
	public Purchase findById(long Purchaseid) {
		
		try {
			return purchaseRepository.findByIdWithDetails(Purchaseid).orElse(null);
		
		} catch (Exception e) {
			log.info("findById: ", e);
			return null;
		}
		
	}

}
