package com.sp.app.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.sp.app.entity.Material;

public interface MaterialService {
	
	public List<Material> listAll();
	public Page<Material> listPage(String schType, String kwd, int current_page, int size);

	public void insertMaterial(Material entity, MultipartFile materialPhoto) throws Exception;
	public void updateMaterial(Material entity) throws Exception;
	public void deleteMaterial(long materialid) throws Exception;
	
	public Material findById(long materialid);
}
