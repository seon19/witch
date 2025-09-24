package com.sp.app.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.RequestList;

public interface RequestListRepository extends JpaRepository<RequestList, Long> {
	boolean existsByRequest_RequestIdAndRequestState(Long requestId, int requestState); // 진행중 사용자있으면 의뢰삭제불가능
	boolean existsByMember_MemberIdAndRequest_RequestIdAndRequestState(Long memberId, Long requestId, int requestState); // 같은 의뢰 중복수락 방지
	long countByMember_MemberIdAndStartRequestDateBetween(Long memberId, LocalDateTime start, LocalDateTime end); // 해당 유저가 오늘 수락한 건수 (하루 3건 제한)
	Page<RequestList> findByMember_MemberIdOrderByStartRequestDateDesc(Long memberId, Pageable pageable); // 의뢰목록 페이징
	}
