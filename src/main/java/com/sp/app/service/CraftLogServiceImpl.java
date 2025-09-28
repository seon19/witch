package com.sp.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.CraftLog;
import com.sp.app.repository.CraftLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class CraftLogServiceImpl implements CraftLogService {
	private final CraftLogRepository craftLogRepository;
	
	@Override
	public List<CraftLog> listAll(long memberId) {
		return craftLogRepository.findByMemberMemberId(memberId);
	}

	@Override
	public Page<CraftLog> listPage(long memberId, String schType, String kwd, int current_page, int size) {
		Page<CraftLog> craftLog = null;
		
		try {
			 Pageable pageable = PageRequest.of(current_page - 1, size, Sort.by(Sort.Direction.DESC, "craftLogId"));
		     craftLog = craftLogRepository.findByMemberMemberId(memberId, pageable);
		} catch (IllegalArgumentException e) {
	        log.warn("잘못된 파라미터: {}", e.getMessage());
	    } catch (Exception e) {
	        log.error("listPage 오류", e);
	    }
		
		return craftLog;
	}


}
