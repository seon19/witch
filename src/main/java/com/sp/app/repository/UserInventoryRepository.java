package com.sp.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Member;

public interface UserInventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByMemberAndMaterial(Member member, Material material);

	Page<Inventory> findByMemberMemberId(Long memberId, Pageable pageable);

	Optional<Inventory> findByMemberMemberIdAndMaterialMaterialId(Long memberId, Long materialId);

	Optional<Inventory> findByMemberMemberIdAndPotionPotionId(Long memberId, Long potionId);
}
