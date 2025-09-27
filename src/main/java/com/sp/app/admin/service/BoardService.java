package com.sp.app.admin.service;

import com.sp.app.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
	Page<Board> list(String kwd, String target, Pageable pageable);
    Board get(Long boardId);
    Long create(Board form, Long writerMemberId);
    void update(Board form);
    void delete(Long boardId);
    void increaseHitCount(Long boardId);
}
