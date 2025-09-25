package com.sp.app.service;

import com.sp.app.entity.Orders;

public interface OrderService {
	
	public Orders purchaseItem(long memberId, long shopId, int quantity) throws IllegalStateException;

}
