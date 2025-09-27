package com.sp.app.admin.service;

import com.sp.app.entity.BoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardReplyService {
    Page<BoardReply> listByBoard(Long boardId, Pageable pageable);
    Long write(Long boardId, Long memberId, String content);
    void delete(Long replyId);
    long countByBoard(Long boardId);
}
