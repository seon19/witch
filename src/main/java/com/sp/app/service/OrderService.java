package com.sp.app.service;

import com.sp.app.dto.UserSaleRequestDTO;
import com.sp.app.entity.Orders;

public interface OrderService {

	public Orders purchaseItem(long memberId, long shopId, int quantity) throws IllegalStateException;

	public void sellItem(Long memberId, UserSaleRequestDTO saleRequest);

	public void sellItem(long memberId, Long itemId, int quantity);

}
