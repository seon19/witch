package com.sp.app.repository;

import com.sp.app.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByVisibilityOrderByNoticeIdDesc(Integer visibility);
    Optional<Notice> findByNoticeIdAndVisibility(Long noticeId, Integer visibility);
}
