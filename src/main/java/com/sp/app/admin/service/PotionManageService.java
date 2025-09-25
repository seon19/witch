package com.sp.app.admin.service;

import java.util.List;

import com.sp.app.entity.Potion;

public interface PotionManageService {
	
	public List<Potion> listAll();
	public Potion findById(long potionId);
	
}
