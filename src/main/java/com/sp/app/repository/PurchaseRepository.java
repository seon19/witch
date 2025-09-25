package com.sp.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	// 재료 이름으로 Purchase 검색
	Page<Purchase> findByMaterial_MaterialNameContaining(String kwd, Pageable pageable);
	
	// 포션 이름으로 Purchase 검색
	Page<Purchase> findByPotion_PotionNameContaining(String kwd, Pageable pageable);

	// 중복 확인
    boolean existsByMaterial_MaterialId(long materialId);
    boolean existsByPotion_PotionId(long potionId);
    
    // 상세 조회
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.material LEFT JOIN FETCH p.potion WHERE p.purchaseId = :purchaseId")
    Optional<Purchase> findByIdWithDetails(@Param("purchaseId") long purchaseId);
    
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.material LEFT JOIN FETCH p.potion")
    List<Purchase> findAllWithDetails();
	
}