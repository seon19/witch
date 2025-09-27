package com.sp.app.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RewardItemDto {
        private Long materialId;
        private String materialName;
        private String materialPhoto;
        private int qty;
    }
}
