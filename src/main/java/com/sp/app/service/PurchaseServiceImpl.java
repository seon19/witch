package com.sp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sp.app.dto.UserSaleRequestDTO;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Purchase;
import com.sp.app.repository.UserInventoryRepository;
import com.sp.app.repository.UserPurchaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final UserPurchaseRepository userpurchaseRepository;
    private final UserInventoryRepository inventoryRepository;

    public Page<UserSaleRequestDTO> getSellableItems(Long memberId, Pageable pageable) {
        // [수정] isAvailable가 true인 아이템만 조회하도록 변경
        Page<Purchase> purchasePage = userpurchaseRepository.findAllByIsAvailable(true, pageable);

        return purchasePage.map(purchase -> {
        	UserSaleRequestDTO dto = new UserSaleRequestDTO();
            // ... DTO 변환 로직 (이전 답변과 동일) ...
            // ... 사용자의 인벤토리를 확인하여 quantityOwned 채우기 ...
            int quantityOwned = 0;
            if (purchase.getMaterial() != null) {
                quantityOwned = inventoryRepository
                    .findByMemberMemberIdAndMaterialMaterialId(memberId, purchase.getMaterial().getMaterialId())
                    .map(Inventory::getQuantity).orElse(0);
            } else if (purchase.getPotion() != null) {
                quantityOwned = inventoryRepository
                    .findByMemberMemberIdAndPotionPotionId(memberId, purchase.getPotion().getPotionId())
                    .map(Inventory::getQuantity).orElse(0);
            }
            dto.setQuantityOwned(quantityOwned);
            
            return dto;
        });
    }

}