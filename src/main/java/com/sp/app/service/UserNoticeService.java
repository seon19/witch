package com.sp.app.service;

import com.sp.app.dto.NoticeDetailDTO;
import com.sp.app.dto.NoticeTitleDTO;
import com.sp.app.entity.Notice;
import com.sp.app.repository.UserNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserNoticeService {

    private final UserNoticeRepository repo;

    @Transactional(readOnly = true)
    public List<NoticeTitleDTO> getTitleList(Integer size) {
        var list = repo.findAllByVisibilityOrderByNoticeIdDesc(1);
        return list.stream()
                .limit(size == null ? Long.MAX_VALUE : size.longValue())
                .map(NoticeTitleDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticeDetailDTO getDetail(Long id) {
        Notice n = repo.findByNoticeIdAndVisibility(id, 1)
                .orElseThrow(() -> new NoSuchElementException("공지 없음"));
        return NoticeDetailDTO.from(n, nicknameOf(n));
    }

    private String nicknameOf(Notice n) {
        return n.getMember() != null ? n.getMember().getNickname() : "운영자";
    }
}
