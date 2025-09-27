package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sp.app.dto.SellableItemDTO;

public interface PurchaseService {

	Page<SellableItemDTO> getSellableItems(Long memberId, Pageable pageable);

}
