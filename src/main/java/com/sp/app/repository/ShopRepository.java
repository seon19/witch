package com.sp.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {

	// 연관된 엔티티의 필드를 참조할 때는 언더스코어(_)
	Page<Shop> findByMaterial_MaterialNameContaining(String kwd, Pageable pageable);

	// materialId를 기준으로 Shop 데이터가 존재하는지 확인
    boolean existsByMaterial_MaterialId(long materialId);
    
    // shopId로 조회할 때, 연관된 material 정보까지 한 번에 가져오는 쿼리
    @Query("SELECT s FROM Shop s JOIN FETCH s.material WHERE s.shopId = :shopId")
    Optional<Shop> findByIdWithMaterial(@Param("shopId") long shopId);
	
}
