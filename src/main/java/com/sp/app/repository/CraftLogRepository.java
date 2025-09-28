package com.sp.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.CraftLog;

public interface CraftLogRepository extends JpaRepository<CraftLog, Long> {
	public Page<CraftLog> findByMemberMemberId(Long memberId, Pageable pageable);
	public List<CraftLog> findByMemberMemberId(Long memberId);
}
