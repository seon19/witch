package com.sp.app.admin.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Material;
import com.sp.app.entity.MaterialReward;
import com.sp.app.entity.Request;
import com.sp.app.repository.MaterialRepository;
import com.sp.app.repository.MaterialRewardRepository;
import com.sp.app.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepo;
    private final MaterialRepository materialRepo;
    private final MaterialRewardRepository materialRewardRepo;
    private final RequestListService requestListService;

    @Override
    @Transactional(readOnly = true)
    public Page<Request> list(String kwd, Integer level, Pageable pageable) {
        return requestRepo.search(kwd, level, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Request get(Long requestId) {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰가 존재하지 않습니다: " + requestId));
    }

    @Override
    public Long create(Request req) {
        // 기본값 보정
        if (req.getRequestDate() == null) req.setRequestDate(LocalDateTime.now());
        if (req.getRewardEnable() == null) req.setRewardEnable(1);
        if (req.getGoalCount() == null) req.setGoalCount(1);

        return requestRepo.save(req).getRequestId();
    }

    @Override
    public void update(Request form) {
        Request saved = get(form.getRequestId());

        saved.setRequestLevel(form.getRequestLevel());
        saved.setRequestName(form.getRequestName());
        saved.setRequestContent(form.getRequestContent());
        saved.setClient(form.getClient());
        saved.setRewardExp(form.getRewardExp());
        saved.setRewardGold(form.getRewardGold());
        saved.setRewardEnable(form.getRewardEnable());
        saved.setGoalCount(form.getGoalCount());
        saved.setDeadline(form.getDeadline()); 

    }

    @Override
    public void delete(Long requestId) {
        if (requestListService.hasAnyInProgressUser(requestId)) {
            throw new IllegalStateException("진행중인 사용자가 있어 삭제할 수 없습니다.");
        }
        requestRepo.deleteById(requestId); 
    }

    @Override
    public void addReward(Long requestId, Long materialId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        Request req = get(requestId);
        Material mat = materialRepo.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("재료가 존재하지 않습니다: " + materialId));

        MaterialReward ex = materialRewardRepo.findByRequest_RequestIdAndMaterial_MaterialId(requestId, materialId).orElse(null);

        if (ex != null) {
            ex.setQty(ex.getQty() + qty);
        } else {
            MaterialReward mr = new MaterialReward();
            mr.setRequest(req);
            mr.setMaterial(mat);
            mr.setQty(qty);
            materialRewardRepo.save(mr); 
        }
    }

    @Override
    public void changeRewardQty(Long requestId, Long materialId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        MaterialReward mr = materialRewardRepo.findByRequest_RequestIdAndMaterial_MaterialId(requestId, materialId).orElseThrow(() -> new IllegalArgumentException("보상재료가 존재하지 않습니다."));
        mr.setQty(qty);
    }

    @Override
    public void removeReward(Long requestId, Long materialId) {
        materialRewardRepo.deleteByRequest_RequestIdAndMaterial_MaterialId(requestId, materialId);
    }
}
