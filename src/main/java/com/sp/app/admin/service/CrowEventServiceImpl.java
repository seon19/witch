package com.sp.app.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.CrowEvent;
import com.sp.app.repository.CrowEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class CrowEventServiceImpl implements CrowEventService {

    private final CrowEventRepository crowEventRepository;

    @Override
    @Transactional
    public void insertEvent(CrowEvent dto) throws Exception {
    	if (crowEventRepository.findOverlappingEvents(dto.getEventStartTime(), dto.getEventEndTime())) {
            throw new RuntimeException("해당 기간에 이미 다른 이벤트가 등록되어 있습니다.");
        }
        crowEventRepository.save(dto);
    }

    @Override
    @Transactional
    public void updateEvent(CrowEvent dto) throws Exception {
    	if (crowEventRepository.findOverlappingEventsForUpdate(dto.getCrowEventId(), dto.getEventStartTime(), dto.getEventEndTime())) {
            throw new RuntimeException("해당 기간에 이미 다른 이벤트가 등록되어 있습니다.");
        }
        crowEventRepository.save(dto);
    }

    @Override
    @Transactional
    public void deleteEvent(long crowEventId) throws Exception {
        crowEventRepository.deleteById(crowEventId);
    }

    @Override
    public CrowEvent findById(long crowEventId) {
        return crowEventRepository.findByIdWithDetails(crowEventId).orElse(null);
    }
    
    @Override
    public Page<CrowEvent> listPage(String schType, String kwd, int currentPage, int size) {
        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.DESC, "crowEventId"));
        
        Page<CrowEvent> p = null;
        if (kwd.isBlank()) {
            p = crowEventRepository.findAllWithDetails(pageable);
        } else if ("eventName".equalsIgnoreCase(schType)) {
            p = crowEventRepository.findByEventNameContaining(kwd, pageable);
        } else {
             p = crowEventRepository.findAllWithDetails(pageable);
        }

        return p;
    }
}
