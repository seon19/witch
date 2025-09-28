package com.sp.app.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.CraftLog;
import com.sp.app.entity.Inventory;
import com.sp.app.entity.Member;
import com.sp.app.entity.Potion;
import com.sp.app.repository.CraftLogRepository;
import com.sp.app.repository.InventoryRepository;
import com.sp.app.repository.PotionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	private final PotionRepository potionRepository;
	private final CraftLogRepository craftLogRepository;

	@Override
	public List<Inventory> listAll(long memberId) {
		 return inventoryRepository.findByMemberMemberId(memberId);
	}

	@Override
	public Page<Inventory> listPage(long memberId, String schType, String kwd, int current_page, int size) {
	    Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "inventoryId"));

	    if ("material".equals(schType)) {
	        return inventoryRepository.findByMemberMemberIdAndMaterialIsNotNull(memberId, pageable);
	    } else if ("potion".equals(schType)) {
	        return inventoryRepository.findByMemberMemberIdAndPotionIsNotNull(memberId, pageable);
	    } else {
	        return inventoryRepository.findAll(pageable);
	    }
	}

	@Override
	@Transactional
	public Inventory findById(long inventoryId) {
		Inventory dto = null;
		
		try {
			Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
			dto = inventory.get();
			
		} catch (NoSuchElementException e) {	
		} catch (Exception e) {
			log.info("findById : ", e);
		}
		return dto;
	}

	@Override
	@Transactional
	public Potion craftPotion(long memberId, long firstMaterialId, long secondMaterialId) throws Exception {
	    // 1. 재료 조회
	    Inventory inv1 = inventoryRepository.findByMemberMemberIdAndMaterialMaterialId(memberId, firstMaterialId)
	            .orElseThrow(() -> new Exception("첫 번째 재료 없음"));
	    Inventory inv2 = inventoryRepository.findByMemberMemberIdAndMaterialMaterialId(memberId, secondMaterialId)
	            .orElseThrow(() -> new Exception("두 번째 재료 없음"));

	    // 2. 재료 차감
	    inv1.setQuantity(inv1.getQuantity() - 1);
	    inv2.setQuantity(inv2.getQuantity() - 1);
	    inventoryRepository.save(inv1);
	    inventoryRepository.save(inv2);

	    // 3. 포션 조회
	    Optional<Potion> optionalPotion = potionRepository
	            .findByFirstMaterial_MaterialIdAndSecondMaterial_MaterialId(firstMaterialId, secondMaterialId);

	    if (optionalPotion.isPresent()) {
	        Potion potion = optionalPotion.get();

	        // 3-1. 포션 인벤토리 추가/갱신
	        Inventory potionInv = inventoryRepository
	                .findByMemberMemberIdAndPotionPotionId(memberId, potion.getPotionId())
	                .orElseGet(() -> new Inventory(inv1.getMember(), potion, 0));

	        potionInv.setQuantity(potionInv.getQuantity() + 1);
	        inventoryRepository.save(potionInv);

	        // 3-2. 로그인한 유저 currentExp 증가
	        int gainExp = potion.getExp() != null ? potion.getExp() : 10; // 디폴트 10
	        Member member = inv1.getMember();
	        member.setCurrentExp(member.getCurrentExp() + gainExp);

	        // 3-3. 성공 로그 추가
	        CraftLog log = new CraftLog();
	        log.setMember(member);
	        log.setPotion(potion);
	        log.setResult("성공");
	        log.setPotionLevel(potion.getPotionLevel());
	        craftLogRepository.save(log);

	        return potion;
	    } else {
	        // 3-4. 실패 로그 추가 (포션 없음)
	    	CraftLog log = new CraftLog();
	        log.setMember(inv1.getMember());
	        log.setPotion(null);
	        log.setResult("실패");
	        log.setPotionLevel(0);
	        craftLogRepository.save(log);

	        // 포션 없으면 null 반환 (재료만 차감)
	        return null;
	    }
	}


	
}