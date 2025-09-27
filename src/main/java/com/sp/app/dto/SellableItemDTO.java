package com.sp.app.dto;

import lombok.Data;

@Data
public class SellableItemDTO {
	private Long purchaseId;
    private String itemName;
    private String itemDescription;
    private String itemPhoto;
    private int sellPrice; // 사용자가 팔 때 받을 가격
    private String itemType;
    private Long itemId;

    // Inventory
    private int quantityOwned; // 사용자가 보유한 수량
}