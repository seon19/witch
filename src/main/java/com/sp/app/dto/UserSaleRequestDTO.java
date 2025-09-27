package com.sp.app.dto;

import lombok.Data;

@Data
public class UserSaleRequestDTO {
    private Long itemId;
    private String itemType;
    private int quantity;
}