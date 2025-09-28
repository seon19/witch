package com.sp.app.dto;

import com.sp.app.entity.Notice;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoticeTitleDTO {
    Long noticeId;
    String noticeName;

    public static NoticeTitleDTO from(Notice n) {
        return NoticeTitleDTO.builder()
                .noticeId(n.getNoticeId())
                .noticeName(n.getNoticeName())
                .build();
    }
}
