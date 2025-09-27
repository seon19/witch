package com.sp.app.repository;

import com.sp.app.entity.BoardLike;
import com.sp.app.entity.BoardLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, BoardLikeId> {
    boolean existsByBoard_BoardIdAndMember_MemberId(Long boardId, Long memberId);
    long countByBoard_BoardId(Long boardId);
    long deleteByBoard_BoardIdAndMember_MemberId(Long boardId, Long memberId);
}
