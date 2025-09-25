package com.sp.app.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sp.app.entity.DailyReward;

public interface DailyRewardRepository extends JpaRepository<DailyReward, Long> {

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM DailyReward r "
			+ "WHERE r.member.memberId = :memberId AND r.rewardDate = :rewardDate")
	boolean existsByMemberIdAndRewardDate(long memberId, LocalDate rewardDate);

	boolean existsByMemberMemberIdAndRewardDateBetween(long memberId, LocalDateTime start, LocalDateTime end);

	// 오늘 받은 보상 내역을 찾기 위한 메소드
	Optional<DailyReward> findTopByMemberMemberIdAndRewardDateBetweenOrderByRewardDateDesc(long memberId, LocalDateTime start, LocalDateTime end);
}
