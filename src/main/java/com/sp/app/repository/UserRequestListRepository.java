package com.sp.app.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sp.app.entity.RequestList;

public interface UserRequestListRepository extends JpaRepository<RequestList, Long> {
    Optional<RequestList> findByMember_MemberIdAndRequest_RequestIdAndRequestState(Long memberId, Long requestId, int state);
    Optional<RequestList> findByRequestListIdAndMember_MemberId(Long requestListId, Long memberId);
    List<RequestList> findTop3ByMember_MemberIdAndRequestStateOrderByStartRequestDateDesc(Long memberId, int requestState);
    List<RequestList> findByMember_MemberIdAndRequestStateIn(Long memberId, Collection<Integer> states);
    Optional<RequestList> findByMember_MemberIdAndRequest_RequestId(Long memberId, Long requestId); 
    boolean existsByMember_MemberIdAndRequest_RequestId(Long memberId, Long requestId);
    
    @Query("select distinct rl.request.requestId from RequestList rl where rl.member.memberId = :memberId")
    List<Long> findTakenRequestIds(@Param("memberId") Long memberId);
}
