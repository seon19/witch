package com.sp.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPurchaseRequestDTO {
    private Long shopId;
    private int quantity;
}