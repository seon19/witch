package com.sp.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.MaterialReward;
import com.sp.app.entity.MaterialRewardId;

public interface MaterialRewardRepository extends JpaRepository<MaterialReward, MaterialRewardId> {
	  List<MaterialReward> findByRequest_RequestId(Long requestId); // 의뢰 보상재료 조회

	  Optional<MaterialReward> findByRequest_RequestIdAndMaterial_MaterialId(Long requestId, Long materialId); // 단건 조회

	  boolean existsByRequest_RequestIdAndMaterial_MaterialId(Long requestId, Long materialId);

	  @Modifying(clearAutomatically = true, flushAutomatically = true)
	  @Transactional
	  void deleteByRequest_RequestIdAndMaterial_MaterialId(Long requestId, Long materialId); // 단건 삭제
	  
	  @Query("select mr from MaterialReward mr join fetch mr.material where mr.request.requestId = :requestId")
	  List<MaterialReward> findByRequestIdWithMaterial(@Param("requestId") Long requestId);
}