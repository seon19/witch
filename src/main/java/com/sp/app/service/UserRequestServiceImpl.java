package com.sp.app.service;

import com.sp.app.dto.UserRequestDTO;
import com.sp.app.entity.*;
import com.sp.app.repository.*;
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
public class UserRequestServiceImpl implements UserRequestService {

    private final UserRequestRepository requestRepository;
    private final UserMaterialRewardRepository materialRewardRepository;
    private final UserRequestListRepository requestListRepository;
    private final MemberRepository memberRepository;
    private final InventoryRepository inventoryRepository;

    private static int safeGoal(Integer goal) {
        return (goal == null || goal <= 0) ? 1 : goal;
    }
    private static int safeCount(Integer cnt) {
        return (cnt == null) ? 0 : cnt;
    }

    @Override
    public Page<UserRequestDTO> availableForUser(Long memberId, Pageable pageable) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + memberId));
        int level = (m.getCurrentLevel() == null) ? 1 : m.getCurrentLevel();

        Page<Request> page = requestRepository.findNewForMember(memberId, level, pageable);
        if (page.getContent().isEmpty()) return Page.empty(pageable);

        List<Long> ids = page.getContent().stream()
                .map(Request::getRequestId)
                .collect(Collectors.toList());

        List<MaterialReward> rewards = materialRewardRepository.findByRequest_RequestIdIn(ids);
        Map<Long, List<MaterialReward>> byReq = rewards.stream()
                .collect(Collectors.groupingBy(r -> r.getRequest().getRequestId()));

        List<UserRequestDTO> cards = page.getContent().stream().map(r -> {
            List<UserRequestDTO.RewardItemDto> items =
                    byReq.getOrDefault(r.getRequestId(), Collections.emptyList())
                            .stream()
                            .map(mr -> UserRequestDTO.RewardItemDto.builder()
                                    .materialId(mr.getMaterial().getMaterialId())
                                    .materialName(mr.getMaterial().getMaterialName())
                                    .materialPhoto(mr.getMaterial().getMaterialPhoto())
                                    .qty(mr.getQty())
                                    .build())
                            .collect(Collectors.toList());

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
        }).collect(Collectors.toList());

        return new PageImpl<>(cards, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public void accept(Long memberId, Long requestId) {
        boolean alreadyTaken = requestListRepository
                .findByMember_MemberIdAndRequest_RequestId(memberId, requestId)
                .isPresent();
        if (alreadyTaken) throw new IllegalStateException("이미 수락했던 의뢰입니다.");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + memberId));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰 없음: " + requestId));

        RequestList rl = new RequestList();
        rl.setMember(member);
        rl.setRequest(request);
        rl.setRequestState(0); 
        rl.setStartRequestDate(LocalDateTime.now());
        rl.setProgressGoal(safeGoal(request.getGoalCount()));
        rl.setProgressCount(0);
        requestListRepository.save(rl);
    }

    @Override
    public List<UserRequestDTO> ongoingForHome(Long memberId) {
        List<RequestList> list = requestListRepository
                .findTop3ByMember_MemberIdAndRequestStateOrderByStartRequestDateDesc(memberId, 0);

        return list.stream().map(rl -> {
            int goal = safeGoal(rl.getProgressGoal());
            Integer owned = resolveProgressFromInventory(rl.getMember().getMemberId(), rl.getRequest());
            int progressCount = (owned != null)
                    ? Math.min(goal, Math.max(0, owned))
                    : safeCount(rl.getProgressCount());
            boolean done = progressCount >= goal;

            return UserRequestDTO.builder()
                    .requestId(rl.getRequest().getRequestId())
                    .requestLevel(rl.getRequest().getRequestLevel())
                    .requestName(rl.getRequest().getRequestName())
                    .goalCount(goal)
                    .requestListId(rl.getRequestListId())
                    .progressCount(progressCount)
                    .progressGoal(goal)
                    .requestState(done ? 1 : rl.getRequestState())
                    .percent((int)Math.floor(100.0 * progressCount / goal))
                    .claimable(done)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public UserRequestDTO detail(Long requestListId, Long memberId) {
        RequestList rl = requestListRepository
                .findByRequestListIdAndMember_MemberId(requestListId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰 진행 내역 없음"));

        List<MaterialReward> rewards =
                materialRewardRepository.findByRequest_RequestId(rl.getRequest().getRequestId());

        return UserRequestDTO.ofDetail(rl, rewards);
    }

    @Override
    @Transactional
    public void increaseProgress(Long requestListId, Long memberId, int step) {
        RequestList rl = requestListRepository
                .findByRequestListIdAndMember_MemberId(requestListId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰 진행 내역 없음"));

        if (hasTarget(rl.getRequest())) {
            ensureUpToDateProgressFromInventory(rl); 
        } else {
            int goal = safeGoal(rl.getProgressGoal());
            int current = safeCount(rl.getProgressCount());
            int newCount = Math.min(goal, Math.max(0, current + step));
            rl.setProgressGoal(goal);
            rl.setProgressCount(newCount);
            if (newCount >= goal && rl.getRequestState() == 0) {
                rl.setRequestState(1);
                rl.setEndRequestDate(LocalDateTime.now());
            }
            requestListRepository.save(rl);
        }
    }

    @Override
    @Transactional
    public UserRequestDTO claimReward(Long requestListId, Long memberId) {
        RequestList rl = requestListRepository
                .findByRequestListIdAndMember_MemberId(requestListId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("의뢰 진행 내역 없음"));

        if (hasTarget(rl.getRequest())) {
            ensureUpToDateProgressFromInventory(rl);
        }

        if (rl.getRequestState() != 1) {
            throw new IllegalStateException("수령할 수 없는 상태입니다.");
        }

        Request req = rl.getRequest();
        Member mem = rl.getMember();

        int goal = safeGoal(rl.getProgressGoal());
        if (req.getRequestItem() != null) {
            consumeTargetItems(mem, req, goal);
        }

        int gold = (req.getRewardGold() == null) ? 0 : req.getRewardGold();
        int exp  = (req.getRewardExp()  == null) ? 0 : req.getRewardExp();

        mem.setCurrentBalance((mem.getCurrentBalance() == null ? 0 : mem.getCurrentBalance()) + gold);
        mem.setCurrentExp((mem.getCurrentExp() == null ? 0 : mem.getCurrentExp()) + exp);
        memberRepository.save(mem);

        List<MaterialReward> rewards =
                materialRewardRepository.findByRequest_RequestId(req.getRequestId());

        for (MaterialReward mr : rewards) {
            Long materialId = mr.getMaterial().getMaterialId();
            int qty = (mr.getQty() == null) ? 0 : mr.getQty();

            Inventory inv = inventoryRepository
                    .findByMember_MemberIdAndMaterial_MaterialId(memberId, materialId)
                    .orElseGet(() -> {
                        Inventory i = new Inventory();
                        i.setMember(mem);
                        i.setMaterial(mr.getMaterial());
                        i.setQuantity(0);
                        return i;
                    });

            inv.setQuantity(safeCount(inv.getQuantity()) + qty);
            inventoryRepository.save(inv);
        }

        rl.setRequestState(2); 
        rl.setEndRequestDate(LocalDateTime.now());
        requestListRepository.save(rl);

        return UserRequestDTO.ofDetail(rl, rewards);
    }

    private boolean hasTarget(Request r) {
        if (r == null || r.getRequestItem() == null) return false;
        return switch (r.getRequestItem()) {
            case MATERIAL -> r.getTargetMaterial() != null;
            case POTION   -> r.getTargetPotion() != null;
        };
    }

    private Integer resolveProgressFromInventory(Long memberId, Request r) {
        if (!hasTarget(r)) return null;

        return switch (r.getRequestItem()) {
            case MATERIAL -> {
                Long mid = r.getTargetMaterial().getMaterialId();
                yield inventoryRepository
                        .findByMember_MemberIdAndMaterial_MaterialId(memberId, mid)
                        .map(Inventory::getQuantity)
                        .orElse(0);
            }
            case POTION -> {
                Long pid = r.getTargetPotion().getPotionId();
                yield inventoryRepository
                        .findByMemberMemberIdAndPotionPotionId(memberId, pid)
                        .map(Inventory::getQuantity)
                        .orElse(0);
            }
        };
    }

    private void ensureUpToDateProgressFromInventory(RequestList rl) {
        int goal = safeGoal(rl.getProgressGoal());
        Integer owned = resolveProgressFromInventory(rl.getMember().getMemberId(), rl.getRequest());
        if (owned == null) return;

        int newCount = Math.min(goal, Math.max(0, owned));
        rl.setProgressGoal(goal);
        rl.setProgressCount(newCount);

        if (newCount >= goal && rl.getRequestState() == 0) {
            rl.setRequestState(1);
            rl.setEndRequestDate(LocalDateTime.now());
        }
        requestListRepository.save(rl);
    }

    private void consumeTargetItems(Member mem, Request req, int needQty) {
        if (needQty <= 0 || req.getRequestItem() == null) return;

        switch (req.getRequestItem()) {
            case MATERIAL -> {
                if (req.getTargetMaterial() == null) return;
                Long materialId = req.getTargetMaterial().getMaterialId();

                Inventory inv = inventoryRepository
                        .findByMember_MemberIdAndMaterial_MaterialId(mem.getMemberId(), materialId)
                        .orElseThrow(() -> new IllegalStateException("보유 재료가 없습니다."));

                int have = safeCount(inv.getQuantity());
                if (have < needQty) {
                    throw new IllegalStateException("재료 수량이 부족합니다. 필요: " + needQty + ", 보유: " + have);
                }
                inv.setQuantity(have - needQty);
                inventoryRepository.save(inv);
            }
            case POTION -> {
                if (req.getTargetPotion() == null) return;
                Long potionId = req.getTargetPotion().getPotionId();

                Inventory inv = inventoryRepository
                        .findByMemberMemberIdAndPotionPotionId(mem.getMemberId(), potionId)
                        .orElseThrow(() -> new IllegalStateException("보유 포션이 없습니다."));

                int have = safeCount(inv.getQuantity());
                if (have < needQty) {
                    throw new IllegalStateException("포션 수량이 부족합니다. 필요: " + needQty + ", 보유: " + have);
                }
                inv.setQuantity(have - needQty);
                inventoryRepository.save(inv);
            }
        }
    }
}
