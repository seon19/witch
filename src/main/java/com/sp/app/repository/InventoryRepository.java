package com.sp.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Member;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	  public Page<Inventory> findByMaterialIsNotNull(Pageable pageable);
	  public Page<Inventory> findByPotionIsNotNull(Pageable pageable);
	  public Optional<Member> findByMemberAndMaterial(Member member, Material material);
	  
	  public List<Inventory> findByMemberMemberId(Long memberId);
	  public Page<Inventory> findByMemberMemberId(Long memberId, Pageable pageable);
	  public Page<Inventory> findByMemberMemberIdAndMaterialIsNotNull(Long memberId, Pageable pageable);
	  public Page<Inventory> findByMemberMemberIdAndPotionIsNotNull(Long memberId, Pageable pageable);
	  
	  public Optional<Inventory> findByMemberMemberIdAndMaterialMaterialId(Long memberId, Long materialId);
	  public Optional<Inventory> findByMemberMemberIdAndPotionPotionId(Long memberId, Long potionId);
	  
	  Optional<Inventory> findByMember_MemberIdAndMaterial_MaterialId(Long memberId, Long materialId);
	  @Query("select coalesce(sum(i.quantity), 0) from Inventory i where i.member.memberId = :memberId and i.material.materialId = :materialId")
	    int sumMaterialQty(@Param("memberId") Long memberId, @Param("materialId") Long materialId);
	  
	  @Query("select coalesce(sum(i.quantity), 0) from Inventory i where i.member.memberId = :memberId and i.potion.potionId = :potionId")
	    int sumPotionQty(@Param("memberId") Long memberId, @Param("potionId") Long potionId);
	}
