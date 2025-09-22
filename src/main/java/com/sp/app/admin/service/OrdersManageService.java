package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Orders;

public interface OrdersManageService {
	
	public List<Orders> listAll();
	public Page<Orders> listPage(String schType, String kwd, int current_page, int size);

	public void insertOrders(Orders entity) throws Exception;
	public void updateOrders(Orders entity) throws Exception;
	public void deleteOrders(long orderid) throws Exception;
	
	public Orders findById(long orderid);

}
