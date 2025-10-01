package com.sp.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.Request;

public interface UserRequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByRewardEnableAndRequestLevelLessThanEqual(int rewardEnable, int level, Pageable pageable);

    @EntityGraph(attributePaths = {"requestRewards"})
    Request getByRequestId(Long id);
    
    List<Request> findTop100ByRewardEnableAndRequestLevelLessThanEqualOrderByRequestLevelAscRequestIdDesc(
            int rewardEnable, int level);
    @Query("select r from Request r where r.rewardEnable = 1 and r.requestLevel <= :level and not exists "
    		+ "(select 1 from RequestList rl where rl.member.memberId = :memberId and rl.request.requestId = r.requestId)"
    		+ " order by r.requestLevel asc, r.requestId desc")
    		Page<Request> findNewForMember(@Param("memberId") Long memberId,
    		                               @Param("level") int level,
    		                               Pageable pageable);


}
