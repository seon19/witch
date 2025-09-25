package com.sp.app.admin.service;

import org.springframework.data.domain.Page;

import com.sp.app.entity.CrowEvent;

public interface CrowEventService {
	
    void insertEvent(CrowEvent dto) throws Exception;
    void updateEvent(CrowEvent dto) throws Exception;
    void deleteEvent(long crowEventId) throws Exception;
    CrowEvent findById(long crowEventId);
    
    Page<CrowEvent> listPage(String schType, String kwd, int currentPage, int size);	
}
