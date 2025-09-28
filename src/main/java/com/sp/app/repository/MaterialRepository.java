package com.sp.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

	public Page<Material> findByMaterialNameContaining(String kwd, Pageable pageable);
	
	public Optional<Material> findByMaterialName(String materialName);
	
	@Query(value = "select m from Material m where (:q is null or :q = '' or lower(m.materialName) like lower(concat('%', :q, '%'))) order by m.materialName asc")
	List<Material> searchByName(@Param("q") String q);
}
