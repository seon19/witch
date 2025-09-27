package com.sp.app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sp.app.entity.Potion;

public interface RecipeService {
	public List<Potion> listAll();
	public List<Potion> findOwnedPotions(long memberId);
	public Page<Potion> listPage(String schType, String kwd, int current_page, int size);
	
	public Potion findById(long potionId);
	public Potion findByPrev(String schType, String kwd, long potionId);
}
