package com.sp.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Member;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	  public Page<Inventory> findByMaterialIsNotNull(Pageable pageable);
	  public Page<Inventory> findByPotionIsNotNull(Pageable pageable);
	  public Optional<Member> findByMemberAndMaterial(Member member, Material material);
}
