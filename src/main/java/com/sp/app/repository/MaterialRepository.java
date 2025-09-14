package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

	public Page<Material> findByMaterialNameContaining(String kwd, Pageable pageable);
}
