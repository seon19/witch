package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	  public Page<Inventory> findByMaterialIsNotNull(Pageable pageable);
	  public Page<Inventory> findByPotionIsNotNull(Pageable pageable);
}
