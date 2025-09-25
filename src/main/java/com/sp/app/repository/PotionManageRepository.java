package com.sp.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Potion;

public interface PotionManageRepository extends JpaRepository<Potion, Long> {

	@Query("SELECT p FROM Potion p JOIN FETCH p.firstMaterial JOIN FETCH p.secondMaterial WHERE p.potionId = :potionId")
    Optional<Potion> findByIdWithMaterials(@Param("potionId") long potionId);

}
