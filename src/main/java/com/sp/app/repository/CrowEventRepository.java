package com.sp.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.CrowEvent;

public interface CrowEventRepository extends JpaRepository<CrowEvent, Long> {

    @Query("SELECT ce FROM CrowEvent ce LEFT JOIN FETCH ce.giveMaterial LEFT JOIN FETCH ce.receiveMaterial LEFT JOIN FETCH ce.saleMaterial")
    List<CrowEvent> findAllWithDetails();

    @Query(value = "SELECT ce FROM CrowEvent ce LEFT JOIN FETCH ce.giveMaterial LEFT JOIN FETCH ce.receiveMaterial LEFT JOIN FETCH ce.saleMaterial",
           countQuery = "SELECT count(ce) FROM CrowEvent ce")
    Page<CrowEvent> findAllWithDetails(Pageable pageable);
    
    Page<CrowEvent> findByEventNameContaining(String kwd, Pageable pageable);

    @Query("SELECT ce FROM CrowEvent ce LEFT JOIN FETCH ce.giveMaterial LEFT JOIN FETCH ce.receiveMaterial LEFT JOIN FETCH ce.saleMaterial WHERE ce.crowEventId = :crowEventId")
    Optional<CrowEvent> findByIdWithDetails(@Param("crowEventId") long crowEventId);
    
    // 기간이 겹치는 이벤트가 있는지 확인
    @Query("SELECT COUNT(ce) > 0 FROM CrowEvent ce WHERE ce.eventStartTime <= :newEndDate AND ce.eventEndTime >= :newStartDate")
    boolean findOverlappingEvents(@Param("newStartDate") LocalDate newStartDate, @Param("newEndDate") LocalDate newEndDate);
    
    // 수정할 때 겹치는 이벤트가 있는지 확인
    @Query("SELECT COUNT(ce) > 0 FROM CrowEvent ce WHERE ce.crowEventId != :eventId AND ce.eventStartTime <= :newEndDate AND ce.eventEndTime >= :newStartDate")
    boolean findOverlappingEventsForUpdate(@Param("eventId") Long eventId, @Param("newStartDate") LocalDate newStartDate, @Param("newEndDate") LocalDate newEndDate);

}

