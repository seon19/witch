package com.sp.app.repository;

import com.sp.app.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByRewardEnableAndRequestLevelLessThanEqual(int rewardEnable, int level, Pageable pageable);

    @EntityGraph(attributePaths = {"requestRewards"})
    Request getByRequestId(Long id);
}
