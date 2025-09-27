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

    @EntityGraph(attributePaths = "member")
    @Query("select b from Board b " +
           "where (:kwd is null or :kwd = '' " +
           "   or lower(b.postName) like lower(concat('%', :kwd, '%')) " +
           "   or lower(b.postContent) like lower(concat('%', :kwd, '%'))) " +
           "order by b.boardId desc")
    Page<Board> findByTitleOrContent(@Param("kwd") String kwd, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select b from Board b join b.member m " +
           "where (:kwd is null or :kwd = '' " +
           "   or lower(coalesce(m.nickname,'')) like lower(concat('%', :kwd, '%')) " +
           "   or lower(coalesce(m.name,''))     like lower(concat('%', :kwd, '%')) " +
           "   or lower(coalesce(m.userId,''))   like lower(concat('%', :kwd, '%'))) " +
           "order by b.boardId desc")
    Page<Board> findByAuthor(@Param("kwd") String kwd, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Board b set b.hitCount = b.hitCount + 1 where b.boardId = :id")
    int increaseHit(@Param("id") Long id);
}
