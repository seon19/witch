package com.sp.app.repository;

import com.sp.app.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = "member")
    @Query("select b from Board b where b.boardId = :id")
    Optional<Board> findWithMemberById(@Param("id") Long id);

    Page<Board> findByPostNameContainingIgnoreCaseOrPostContentContainingIgnoreCase(
            String title, String content, Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Board b set b.hitCount = b.hitCount + 1 where b.boardId = :id")
    int increaseHit(@Param("id") Long id);
}
