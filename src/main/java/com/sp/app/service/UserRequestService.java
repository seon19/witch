// com.sp.app.service.UserRequestService.java
package com.sp.app.service;

import com.sp.app.dto.UserRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRequestService {
    Page<UserRequestDTO> availableForUser(Long memberId, Pageable pageable);
    void accept(Long memberId, Long requestId);
    List<UserRequestDTO> ongoingForHome(Long memberId);
    UserRequestDTO detail(Long requestListId, Long memberId);
    void increaseProgress(Long requestListId, Long memberId, int step);
    UserRequestDTO claimReward(Long requestListId, Long memberId);
}
