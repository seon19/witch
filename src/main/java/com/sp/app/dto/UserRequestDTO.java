package com.sp.app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sp.app.entity.MaterialReward;
import com.sp.app.entity.Request;
import com.sp.app.entity.RequestList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestDTO {

    private Long requestId;
    private int requestLevel;
    private String requestName;
    private String requestContent;
    private String client;
    private int rewardExp;
    private int rewardGold;
    private int goalCount;

    private List<RewardItemDto> rewardItems;

    private Long requestListId;
    private int progressCount;
    private int progressGoal;
    private int requestState;
    private int percent;
    private boolean claimable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RewardItemDto {
        private Long materialId;
        private String materialName;
        private String materialPhoto;
        private int qty;
    }

    private static int toPercent(int count, int goal) {
        if (goal <= 0) return 0;
        int p = (int) Math.floor(100.0 * count / goal);
        return Math.min(100, Math.max(0, p));
    }

    public static UserRequestDTO ofList(RequestList rl) {
        Request r = rl.getRequest();
        return UserRequestDTO.builder()
                .requestId(r.getRequestId())
                .requestLevel(r.getRequestLevel())
                .requestName(r.getRequestName())
                .goalCount(r.getGoalCount())
                .requestListId(rl.getRequestListId())
                .progressCount(rl.getProgressCount())
                .progressGoal(rl.getProgressGoal())
                .requestState(rl.getRequestState())
                .percent(toPercent(rl.getProgressCount(), rl.getProgressGoal()))
                .claimable(rl.getRequestState() == 1)
                .build();
    }

    public static UserRequestDTO ofDetail(RequestList rl, List<MaterialReward> rewards) {
        Request r = rl.getRequest();

        List<UserRequestDTO.RewardItemDto> items = rewards.stream()
        	    .map(v -> UserRequestDTO.RewardItemDto.builder()
        	        .materialId(v.getMaterial().getMaterialId())
        	        .materialName(v.getMaterial().getMaterialName())
        	        .materialPhoto(v.getMaterial().getMaterialPhoto())
        	        .qty(v.getQty())
        	        .build())
        	    .collect(java.util.stream.Collectors.toList());  

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
                .requestListId(rl.getRequestListId())
                .progressCount(rl.getProgressCount())
                .progressGoal(rl.getProgressGoal())
                .requestState(rl.getRequestState())
                .percent(toPercent(rl.getProgressCount(), rl.getProgressGoal()))
                .claimable(rl.getRequestState() == 1)
                .build();
    }
}
