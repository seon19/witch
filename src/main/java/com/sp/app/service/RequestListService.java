package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sp.app.entity.RequestList;

public interface RequestListService {
	Page<RequestList> listMyRequests(Long memberId, Pageable pageable); // 마이페이지에서 의뢰목록 확인
	RequestList get(Long requestListId); // 의뢰내역 확인
}
