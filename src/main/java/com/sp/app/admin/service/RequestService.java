package com.sp.app.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sp.app.entity.Request;

public interface RequestService {
    Page<Request> list(String kwd, Integer level, Pageable pageable);
    Request get(Long requestId);
    Long create(Request req);
    void update(Request req);
    void delete(Long requestId);

    void addReward(Long requestId, Long materialId, int qty);
    void changeRewardQty(Long requestId, Long materialId, int qty);
    void removeReward(Long requestId, Long materialId);
    
    Long createWithTarget(Request req, Request.RequestItem requestItem, Long materialId, Long potionId);
	void updateWithTarget(Request req, Request.RequestItem requestItem, Long materialId, Long potionId);
	void setTargetMaterial(Long requestId, Long materialId);
	void setTargetPotion(Long requestId, Long potionId);
	boolean canDelete(Long requestId);
}
