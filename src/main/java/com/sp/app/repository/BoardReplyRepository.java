package com.sp.app.repository;

import com.sp.app.entity.BoardReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {
    Page<BoardReply> findByBoard_BoardIdOrderByRegDateDesc(Long boardId, Pageable pageable);
    long countByBoard_BoardId(Long boardId);
    long deleteByBoard_BoardId(Long boardId);
}
