package com.sp.app.admin.service;

public interface RequestListService {
    Long accept(Long memberId, Long requestId); // 의뢰 수락 (하루 3건 제한 + 중복 수락 방지)
    void increaseProgress(Long requestListId, int plus); // 진행도 증가 — 목표 달성 시 자동 완료 처리
    boolean hasAnyInProgressUser(Long requestId); // 해당 의뢰를 진행중인 사용자가 있는지 확인
}
