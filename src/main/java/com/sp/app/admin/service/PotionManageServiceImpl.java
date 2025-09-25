package com.sp.app.admin.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Potion;
import com.sp.app.repository.PotionManageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class PotionManageServiceImpl implements PotionManageService {
	private final PotionManageRepository potionManageRepository;
	
	@Override
    public List<Potion> listAll() {
        return potionManageRepository.findAll();
    }

	@Override
	public Potion findById(long potionId) {
		return potionManageRepository.findByIdWithMaterials(potionId).orElse(null);
	}
}
