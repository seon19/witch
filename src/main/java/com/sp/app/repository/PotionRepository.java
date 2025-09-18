package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Potion;

public interface PotionRepository extends JpaRepository<Potion, Long> {
	public Page<Potion> findByPotionNameContaining(String kwd, Pageable pageable);
}
