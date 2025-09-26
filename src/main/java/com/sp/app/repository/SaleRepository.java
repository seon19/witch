package com.sp.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Shop;

public interface SaleRepository extends JpaRepository<Shop, Long> {

}
