package com.sp.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
}
