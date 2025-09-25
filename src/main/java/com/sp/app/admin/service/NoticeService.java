package com.sp.app.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sp.app.entity.Notice;

public interface NoticeService {

    Page<Notice> list(String kwd, Integer visibility, String target, Pageable pageable);
    Notice get(Long noticeId);
    Long create(Notice form, Long writerMemberId);
    void update(Notice form);
    void delete(Long noticeId);
    void changeVisibility(Long noticeId, int visibility); 
}
