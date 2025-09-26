package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.dto.UserInventoryDTO;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Shop;
import com.sp.app.repository.UserInventoryRepository;
import com.sp.app.repository.UserShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class UserShopServiceImpl implements UserShopService {

	private final UserShopRepository shopRepository;
	private final UserInventoryRepository inventoryRepository;
	
    @Override
    public Page<Shop> listAvailableShopItems(int currentPage, int size) {
        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.DESC, "shopId")); 
        
        return shopRepository.findByIsAvailableTrue(pageable);
    }

    
    @Override
    public Page<UserInventoryDTO> listUserInventory(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Inventory> inventoryPage = inventoryRepository.findByMemberMemberId(memberId, pageable);

        return inventoryPage.map(inventory -> {
            UserInventoryDTO dto = new UserInventoryDTO();
            dto.setQuantityOwned(inventory.getQuantity());

            // Material이 있는지 확인
            if (inventory.getMaterial() != null) {
                dto.setItemType("MATERIAL");
                dto.setItemId(inventory.getMaterial().getMaterialId());
                dto.setItemName(inventory.getMaterial().getMaterialName());
                dto.setItemDescription(inventory.getMaterial().getMaterialDescription());
                dto.setItemPhoto(inventory.getMaterial().getMaterialPhoto());
                dto.setSellPrice(inventory.getMaterial().getMaterialPrice());
            
            // Potion이 있는지 확인
            } else if (inventory.getPotion() != null) {
                dto.setItemType("POTION");
                dto.setItemId(inventory.getPotion().getPotionId());
                dto.setItemName(inventory.getPotion().getPotionName());
                dto.setItemDescription(inventory.getPotion().getPotionDescription());
                dto.setItemPhoto(inventory.getPotion().getPotionPhoto());
            }
            return dto;
        });
    }

}
