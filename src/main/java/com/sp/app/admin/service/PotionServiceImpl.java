package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Potion;
import com.sp.app.repository.PotionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class PotionServiceImpl implements PotionService {
	private final PotionRepository potionRepository;
	
	@Override
	public List<Potion> listAll() {
		List<Potion> potionList = potionRepository.findAll();
		return potionList;
	}

	@Override
	public Page<Potion> listPage(String schType, String kwd, int current_page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertPotion(Potion entity) throws Exception {
		
		
	}

	@Override
	public void updatePotion(Potion entity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePotion(long potionId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Potion findById(long potionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Potion findByPrev(String schType, String kwd, long potionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Potion findByNext(String schType, String kwd, long potionId) {
		// TODO Auto-generated method stub
		return null;
	}

}
