package com.sp.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.DailyReward;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Member;
import com.sp.app.repository.DailyRewardRepository;
import com.sp.app.repository.MaterialRepository;
import com.sp.app.repository.MemberRepository;
import com.sp.app.repository.UserInventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final DailyRewardRepository dailyRewardRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final MaterialRepository materialRepository;
    private final MemberRepository memberRepository;

    private final Random random = new Random();

    @Override
    public boolean hasReceivedTodayReward(long memberId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return dailyRewardRepository.existsByMemberMemberIdAndRewardDateBetween(memberId, start, end);
    }


    @Transactional
    @Override
    public DailyReward giveDailyReward(long memberId) {

        List<Material> materials = materialRepository.findAll();
        Material material = materials.get(random.nextInt(materials.size()));

        int quantity = random.nextInt(2) + 1;

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        DailyReward reward = new DailyReward();
        reward.setMember(member); 
        reward.setMaterial(material);
        reward.setQuantity(quantity);
        reward.setRewardDate(LocalDateTime.now());
        dailyRewardRepository.save(reward);

        Inventory inventory = userInventoryRepository.findByMemberAndMaterial(member, material)
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setMember(member);
                    newInv.setMaterial(material);
                    newInv.setQuantity(0);
                    return newInv;
                });
        inventory.setQuantity(inventory.getQuantity() + quantity);
        userInventoryRepository.save(inventory);

        return reward;
    }

    @Override
    public int updateConsecutiveDays(long memberId) {
        return 1;
    }
    
    @Override
    public DailyReward getTodayReward(long memberId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return dailyRewardRepository.findTopByMemberMemberIdAndRewardDateBetweenOrderByRewardDateDesc(memberId, start, end)
                .orElse(null);
    }
}
