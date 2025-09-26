package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sp.app.dto.UserSaleRequestDTO;

public interface PurchaseService {

	Page<UserSaleRequestDTO> getSellableItems(Long memberId, Pageable pageable);

}
