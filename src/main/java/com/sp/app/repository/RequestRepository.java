package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

	@Query(value = "SELECT r FROM Request r WHERE ( :kwd IS NULL OR :kwd = '' OR r.requestName    LIKE CONCAT('%', :kwd, '%') OR r.requestContent LIKE CONCAT('%', :kwd, '%') ) " +
			    " AND ( :level IS NULL OR r.requestLevel = :level ) ORDER BY r.requestId DESC",
		   countQuery = "SELECT COUNT(r) FROM Request r WHERE ( :kwd IS NULL OR :kwd = '' OR r.requestName LIKE CONCAT('%', :kwd, '%') OR " +
			    " r.requestContent LIKE CONCAT('%', :kwd, '%') ) AND ( :level IS NULL OR r.requestLevel = :level )")
	Page<Request> search(@Param("kwd") String kwd,@Param("level") Integer level,Pageable pageable);

}