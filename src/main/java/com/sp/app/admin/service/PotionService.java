package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Potion;

public interface PotionService {
	public List<Potion> listAll();
	public Page<Potion> listPage(String schType, String kwd, int current_page, int size);
	
	public void insertPotion(Potion entity) throws Exception;
	public void updatePotion(Potion entity) throws Exception;
	public void deletePotion(long potionId) throws Exception;
	
	public Potion findById(long potionId);
	public Potion findByPrev(String schType, String kwd, long potionId);
	public Potion findByNext(String schType, String kwd, long potionId);
}
