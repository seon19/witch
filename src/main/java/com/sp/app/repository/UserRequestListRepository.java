package com.sp.app.repository;

import com.sp.app.entity.RequestList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRequestListRepository extends JpaRepository<RequestList, Long> {
    Optional<RequestList> findByMember_MemberIdAndRequest_RequestIdAndRequestState(Long memberId, Long requestId, int requestState);
    List<RequestList> findTop5ByMember_MemberIdAndRequestStateOrderByStartRequestDateDesc(Long memberId, int requestState);
}
