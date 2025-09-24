package com.sp.app.admin.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Member;
import com.sp.app.entity.Request;
import com.sp.app.entity.RequestList;
import com.sp.app.repository.MemberRepository;
import com.sp.app.repository.RequestListRepository;
import com.sp.app.repository.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestListServiceImpl implements RequestListService {

	private final RequestListRepository requestListRepo;
	private final RequestRepository requestRepo;
	private final MemberRepository memberRepo;

	public static final int STATE_IN_PROGRESS = 0;
	public static final int STATE_COMPLETED   = 1;
	public static final int STATE_EXPIRED     = 2;
	
	private static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");
	
	@Override
	public Long accept(Long memberId, Long requestId) {
	    Request req = requestRepo.findById(requestId)
	            .orElseThrow(() -> new IllegalArgumentException("의뢰가 존재하지 않습니다: " + requestId));
	    Member mem = memberRepo.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + memberId));
	
	    LocalDateTime start = LocalDate.now(ZONE_SEOUL).atStartOfDay();
	    LocalDateTime end   = start.plusDays(1);
	    long todayCount = requestListRepo.countByMember_MemberIdAndStartRequestDateBetween(memberId, start, end);
	    if (todayCount >= 3) {
	        throw new IllegalStateException("하루 수락 가능 건수를 초과했습니다.");
	    }
	
	    boolean dup = requestListRepo
	            .existsByMember_MemberIdAndRequest_RequestIdAndRequestState(
	                    memberId, requestId, STATE_IN_PROGRESS);
	    if (dup) {
	        throw new IllegalStateException("이미 진행중인 동일 의뢰가 있습니다.");
	    }
	
	    RequestList rl = new RequestList();
	    rl.setMember(mem);
	    rl.setRequest(req);
	    rl.setStartRequestDate(LocalDateTime.now(ZONE_SEOUL));
	    rl.setRequestState(STATE_IN_PROGRESS);
	    rl.setEndRequestDate(null);
	
	    Integer goal = req.getGoalCount() == null ? 1 : req.getGoalCount();
	    rl.setProgressGoal(goal);
	    rl.setProgressCount(0);
	
	    return requestListRepo.save(rl).getRequestListId();
	}
	
	@Override
	public void increaseProgress(Long requestListId, int plus) {
	    if (plus <= 0) return;
	    RequestList rl = requestListRepo.findById(requestListId)
	            .orElseThrow(() -> new IllegalArgumentException("의뢰 수락 기록이 없습니다: " + requestListId));
	
	    if (rl.getRequestState() != STATE_IN_PROGRESS) return;
	
	    int next = Math.min(rl.getProgressGoal(), rl.getProgressCount() + plus);
	    rl.setProgressCount(next);
	
	    if (next >= rl.getProgressGoal()) {
	        rl.setRequestState(STATE_COMPLETED);
	        rl.setEndRequestDate(LocalDateTime.now(ZONE_SEOUL));
	    }
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyInProgressUser(Long requestId) {
	    return requestListRepo.existsByRequest_RequestIdAndRequestState(requestId, STATE_IN_PROGRESS);
	}
	
}