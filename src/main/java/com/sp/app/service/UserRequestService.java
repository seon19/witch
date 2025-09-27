// com.sp.app.request.service.UserRequestService
package com.sp.app.service;

import com.sp.app.entity.*;
import com.sp.app.repository.*;
import com.sp.app.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRequestService {

    private final UserRequestRepository requestRepository;
    private final UserMaterialRewardRepository materialRewardRepository;
    private final UserRequestListRepository requestListRepository;
    private final MemberRepository memberRepository;

    public Page<UserRequestDTO> availableForUser(Long memberId, Pageable pageable) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + memberId));
        int level = Optional.ofNullable(m.getCurrentLevel()).orElse(1);

        Page<Request> page = requestRepository
                .findByRewardEnableAndRequestLevelLessThanEqual(1, level, pageable);

        if (page.isEmpty()) return Page.empty(pageable);

        List<Long> ids = page.getContent().stream().map(Request::getRequestId).toList();
        List<MaterialReward> rewards = materialRewardRepository.findByRequest_RequestIdIn(ids);

        Map<Long, List<MaterialReward>> byReq =
                rewards.stream().collect(Collectors.groupingBy(r -> r.getRequest().getRequestId()));

        List<UserRequestDTO> cards = page.getContent().stream().map(r -> {
            List<UserRequestDTO.RewardItemDto> items = byReq.getOrDefault(r.getRequestId(), List.of())
                    .stream().map(mr -> UserRequestDTO.RewardItemDto.builder()
                            .materialId(mr.getMaterial().getMaterialId())
                            .materialName(mr.getMaterial().getMaterialName())
                            .materialPhoto(mr.getMaterial().getMaterialPhoto())
                            .qty(mr.getQty())
                            .build())
                    .toList();

            return UserRequestDTO.builder()
                    .requestId(r.getRequestId())
                    .requestLevel(r.getRequestLevel())
                    .requestName(r.getRequestName())
                    .requestContent(r.getRequestContent())
                    .client(r.getClient())
                    .rewardExp(r.getRewardExp())
                    .rewardGold(r.getRewardGold())
                    .goalCount(r.getGoalCount())
                    .rewardItems(items)
                    .build();
        }).toList();

        return new PageImpl<>(cards, pageable, page.getTotalElements());
    }

    @Transactional
    public void accept(Long memberId, Long requestId) {
 
        if (requestListRepository
                .findByMember_MemberIdAndRequest_RequestIdAndRequestState(memberId, requestId, 0)
                .isPresent()) {
            throw new IllegalStateException("이미 진행중인 의뢰입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + memberId));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰 없음: " + requestId));

        RequestList rl = new RequestList();
        rl.setMember(member);
        rl.setRequest(request);
        rl.setRequestState(0);
        rl.setStartRequestDate(LocalDateTime.now());
        rl.setProgressGoal(Optional.ofNullable(request.getGoalCount()).orElse(1));
        rl.setProgressCount(0);
        requestListRepository.save(rl);
    }

    public List<RequestList> ongoingForHome(Long memberId) {
        return requestListRepository
                .findTop5ByMember_MemberIdAndRequestStateOrderByStartRequestDateDesc(memberId, 0);
    }
}
