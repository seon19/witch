package com.sp.app.dto;

import com.sp.app.entity.Notice;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NoticeDetailDTO {
    Long noticeId;
    String noticeName;
    String noticeContent;
    LocalDateTime noticeDate;
    String writerNickname;   

    public static NoticeDetailDTO from(Notice n, String nickname) {
        return NoticeDetailDTO.builder()
                .noticeId(n.getNoticeId())
                .noticeName(n.getNoticeName())
                .noticeContent(n.getNoticeContent())
                .noticeDate(n.getNoticeDate())
                .writerNickname(nickname)
                .build();
    }
}
