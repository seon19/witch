package com.sp.app.repository;

import com.sp.app.entity.RequestList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRequestListRepository extends JpaRepository<RequestList, Long> {
    Optional<RequestList> findByMember_MemberIdAndRequest_RequestIdAndRequestState(Long memberId, Long requestId, int state);
    Optional<RequestList> findByRequestListIdAndMember_MemberId(Long requestListId, Long memberId);
    List<RequestList> findTop3ByMember_MemberIdAndRequestStateOrderByStartRequestDateDesc(Long memberId, int requestState);
}
