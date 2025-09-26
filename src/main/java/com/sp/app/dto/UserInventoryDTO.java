package com.sp.app.dto;

import lombok.Data;

@Data
public class UserInventoryDTO {
	private Long itemId;
    private String itemType; // "MATERIAL" 또는 "POTION"
    private String itemName;
    private String itemDescription;
    private String itemPhoto;
    private long sellPrice;
    private int quantityOwned;
}