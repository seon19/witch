package com.sp.app.repository;

import com.sp.app.entity.MaterialReward;
import com.sp.app.entity.MaterialRewardId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserMaterialRewardRepository extends JpaRepository<MaterialReward, MaterialRewardId> {
    List<MaterialReward> findByRequest_RequestIdIn(Collection<Long> requestIds);
    List<MaterialReward> findByRequest_RequestId(Long requestId);
}
