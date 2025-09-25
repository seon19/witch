package com.sp.app.repository;

import com.sp.app.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @EntityGraph(attributePaths = "member")
    @Query("select n from Notice n where (:kwd is null or :kwd = '' or lower(n.noticeName)   like lower(concat('%', :kwd, '%')) or lower(n.noticeContent) like lower(concat('%', :kwd, '%'))) and (:visibility is null or n.visibility = :visibility) order by n.noticeId desc")
    Page<Notice> findByTitleOrContent(@Param("kwd") String kwd, @Param("visibility") Integer visibility, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select n from Notice n join n.member m where (:kwd is null or :kwd = '' or lower(coalesce(m.nickname, '')) like lower(concat('%', :kwd, '%')) or lower(coalesce(m.name, ''))     like lower(concat('%', :kwd, '%')) or"
            + " lower(coalesce(m.userId, ''))   like lower(concat('%', :kwd, '%'))) and (:visibility is null or n.visibility = :visibility) order by n.noticeId desc")
    Page<Notice> findByAuthor(@Param("kwd") String kwd,@Param("visibility") Integer visibility, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select n from Notice n where n.noticeId = :id")
    Optional<Notice> findWithWriterById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notice n set n.visibility = :visibility, n.noticeUpdateDate = :updatedAt where n.noticeId = :id")
    int updateVisibility(@Param("id") Long id,
                         @Param("visibility") int visibility,
                         @Param("updatedAt") LocalDateTime updatedAt);

    long countByVisibility(int visibility);
}
