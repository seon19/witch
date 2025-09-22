package com.sp.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Orders;


public interface OrdersRepository extends JpaRepository<Orders, Long> {

	
}
