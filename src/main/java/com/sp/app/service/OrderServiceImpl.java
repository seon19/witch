package com.sp.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.dto.UserSaleRequestDTO;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Material;
import com.sp.app.entity.Member;
import com.sp.app.entity.OrderItem;
import com.sp.app.entity.Orders;
import com.sp.app.entity.Shop;
import com.sp.app.entity.UserWallet;
import com.sp.app.repository.InventoryRepository;
import com.sp.app.repository.MemberRepository;
import com.sp.app.repository.OrdersRepository;
import com.sp.app.repository.ShopRepository;
import com.sp.app.repository.UserInventoryRepository;
import com.sp.app.repository.UserWalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final OrdersRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final UserWalletRepository userWalletRepository;

    @Override
    @Transactional
    public Orders purchaseItem(long memberId, long shopId, int quantity) throws IllegalStateException {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        Shop shopItem = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalStateException("판매 상품 정보를 찾을 수 없습니다."));

        Material material = shopItem.getMaterial();

        int totalPrice = shopItem.getSellingPrice() * quantity;
        if (member.getCurrentBalance() < totalPrice) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        Orders order = new Orders();
        order.setMember(member);
        order.setTotalPrice(totalPrice);

        OrderItem orderItem = new OrderItem();
        orderItem.setMaterial(material);
        orderItem.setPrice(shopItem.getSellingPrice());
        orderItem.setQuantity(quantity);
        order.addOrderItem(orderItem);

        Orders savedOrder = orderRepository.save(order);

        long newBalance = member.getCurrentBalance() - totalPrice;
        member.setCurrentBalance(newBalance);
        Member updatedMember = memberRepository.save(member);
        
        memberRepository.flush();

        UserWallet walletTransaction = new UserWallet();
        walletTransaction.setMember(updatedMember);
        walletTransaction.setChangeReason("상품 구매: " + material.getMaterialName());
        walletTransaction.setChangeAmount(-totalPrice);
        userWalletRepository.save(walletTransaction);

        Optional<Inventory> inventoryOptional = userInventoryRepository.findByMemberAndMaterial(member, material);
        Inventory inventory;

        if (inventoryOptional.isPresent()) {
            inventory = inventoryOptional.get();
        } else {
            inventory = new Inventory();
            inventory.setMember(updatedMember);
            inventory.setMaterial(material);
            inventory.setQuantity(0);
        }

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

        return savedOrder;
    }
    
    @Transactional
    public void sellItem(Long memberId, UserSaleRequestDTO saleRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        Inventory inventoryItem;
        long sellPrice = 0;
        
        if ("MATERIAL".equals(saleRequest.getItemType())) {
            inventoryItem = userInventoryRepository.findByMemberMemberIdAndMaterialMaterialId(memberId, saleRequest.getItemId())
                .orElseThrow(() -> new IllegalStateException("판매할 아이템을 보유하고 있지 않습니다."));
            sellPrice = inventoryItem.getMaterial().getMaterialPrice();

        } else if ("POTION".equals(saleRequest.getItemType())) {
            inventoryItem = userInventoryRepository.findByMemberMemberIdAndPotionPotionId(memberId, saleRequest.getItemId())
                .orElseThrow(() -> new IllegalStateException("판매할 아이템을 보유하고 있지 않습니다."));

        } else {
            throw new IllegalArgumentException("알 수 없는 아이템 타입입니다.");
        }

        if (inventoryItem.getQuantity() < saleRequest.getQuantityOwned()) {
            throw new IllegalStateException("보유 수량이 부족합니다.");
        }

        int newQuantity = inventoryItem.getQuantity() - saleRequest.getQuantityOwned();
        if (newQuantity > 0) {
            inventoryItem.setQuantity(newQuantity);
        } else {
            inventoryRepository.delete(inventoryItem);
        }

        long totalRevenue = sellPrice * saleRequest.getQuantityOwned();
        member.setCurrentBalance(member.getCurrentBalance() + totalRevenue);
    }

	@Override
	public void sellItem(long memberId, Long itemId, int quantity) {
		// TODO Auto-generated method stub
		
	}

	

}

