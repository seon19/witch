package com.sp.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Potion;

public interface RecipeRepository extends JpaRepository<Potion, Long> {
	public Page<Potion> findByPotionNameContaining(String kwd, Pageable pageable);
	
	@Query(value = "SELECT * FROM potion WHERE potionId > :potionId ORDER BY potionId ASC LIMIT 1", nativeQuery = true)
	public Potion findByPrev(@Param("potionId") long potionId);
	@Query(value = "SELECT * FROM potion WHERE potionId>:potionId AND potionName LIKE '%'||:kwd||'%' ORDER BY potionId ASC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	public Potion findByPrevName(@Param("potionId") long potionId, @Param("kwd") String kwd);
	@Query(value = "SELECT * FROM potion WHERE potionId>:potionId AND (subject LIKE '%'||:kwd||'%' OR content LIKE '%'||:kwd||'%') ORDER BY potionId ASC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	public Potion findByPrevAll(@Param("potionId") long potionId, @Param("kwd") String kwd);

	@Query(value = "SELECT p.* FROM potion p " + "JOIN inventory i ON p.potionId = i.potionId " + "WHERE i.memberId = :memberId " +"GROUP BY p.potionId", nativeQuery = true)
	public List<Potion> findOwnedPotions(@Param("memberId") long memberId);
}
