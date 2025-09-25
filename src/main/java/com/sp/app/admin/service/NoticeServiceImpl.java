package com.sp.app.admin.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Member;
import com.sp.app.entity.Notice;
import com.sp.app.repository.MemberRepository;
import com.sp.app.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Override
    public Page<Notice> list(String kwd, Integer visibility, String target, Pageable pageable) {
        if ("AUTHOR".equalsIgnoreCase(target)) {
            return noticeRepository.findByAuthor(kwd, visibility, pageable);
        } else {
            return noticeRepository.findByTitleOrContent(kwd, visibility, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Notice get(Long noticeId) {
        return noticeRepository.findWithWriterById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다: " + noticeId));
    }

    @Override
    public Long create(Notice form, Long writerMemberId) {
        Member writer = memberRepository.getReferenceById(writerMemberId);
        form.setMember(writer);
        if (form.getNoticeDate() == null) form.setNoticeDate(LocalDateTime.now());
        form.setNoticeUpdateDate(null);
        if (form.getVisibility() == null) form.setVisibility(1);
        return noticeRepository.save(form).getNoticeId();
    }

    @Override
    public void update(Notice form) {
        Notice saved = noticeRepository.findById(form.getNoticeId())
            .orElseThrow(() -> new IllegalArgumentException("공지 없음: " + form.getNoticeId()));

        saved.setNoticeName(form.getNoticeName());
        saved.setNoticeContent(form.getNoticeContent());
        saved.setVisibility(form.getVisibility());
        saved.setNoticeUpdateDate(LocalDateTime.now());
       
    }

    @Override
    public void delete(Long noticeId) {
    	noticeRepository.deleteById(noticeId);
    }

    @Override
    public void changeVisibility(Long noticeId, int visibility) {
        validateVisibility(visibility);

        int updated = noticeRepository.updateVisibility(noticeId, visibility, LocalDateTime.now());
        if (updated == 0) {
            throw new IllegalArgumentException("공지사항이 존재하지 않습니다: " + noticeId);
        }
    }

    private void validateVisibility(int visibility) {
        if (visibility != 0 && visibility != 1) {
            throw new IllegalArgumentException("visibility는 0 또는 1만 허용됩니다.");
        }
    }
}
