package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sp.app.dto.SellableItemDTO;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Potion;
import com.sp.app.entity.Purchase;
import com.sp.app.repository.UserInventoryRepository;
import com.sp.app.repository.UserPurchaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final UserPurchaseRepository userPurchaseRepository;
    private final UserInventoryRepository userInventoryRepository;

    public Page<SellableItemDTO> getSellableItems(Long memberId, Pageable pageable) {

        Page<Purchase> purchasePage = userPurchaseRepository.findAllByIsAvailable(true, pageable);

        return purchasePage.map(purchase -> {
            SellableItemDTO dto = new SellableItemDTO();
            
            dto.setPurchaseId(purchase.getPurchaseId());
            dto.setSellPrice(purchase.getPurchasePrice()); 

            int quantityOwned = 0;
            
            if (purchase.getMaterial() != null) {
                Material item = purchase.getMaterial();
                dto.setItemType("MATERIAL");
                dto.setItemId(item.getMaterialId());
                dto.setItemName(item.getMaterialName());
                dto.setItemDescription(item.getMaterialDescription());
                dto.setItemPhoto(item.getMaterialPhoto());
                
                quantityOwned = userInventoryRepository 
                    .findByMemberMemberIdAndMaterialMaterialId(memberId, item.getMaterialId())
                    .map(Inventory::getQuantity).orElse(0);

            } else if (purchase.getPotion() != null) {
                Potion item = purchase.getPotion();
                dto.setItemType("POTION");
                dto.setItemId(item.getPotionId());
                dto.setItemName(item.getPotionName());
                dto.setItemDescription(item.getPotionDescription());
                dto.setItemPhoto(item.getPotionPhoto());
                
                quantityOwned = userInventoryRepository
                    .findByMemberMemberIdAndPotionPotionId(memberId, item.getPotionId())
                    .map(Inventory::getQuantity).orElse(0);
            }
            
            dto.setQuantityOwned(quantityOwned);
            return dto;
        });
    }

}