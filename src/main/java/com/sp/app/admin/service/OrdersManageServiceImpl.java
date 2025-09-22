package com.sp.app.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Orders;
import com.sp.app.repository.OrdersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersManageServiceImpl implements OrdersManageService {

	private final OrdersRepository ordersRepository;
	
	@Value("${file.upload-path.root}")
	private String uploadPathRoot;

	@Override
	public List<Orders> listAll() {
		List<Orders> list = ordersRepository.findAll();
		return list;
	}

	@Override
	public Page<Orders> listPage(String schType, String kwd, int current_page, int size) {
		Page<Orders> p = null;
		
		try {
			Pageable pageable = PageRequest.of(current_page -1, size, Sort.by(Sort.Direction.DESC, "orderid"));
			
			if(kwd.isBlank()) {
				p = ordersRepository.findAll(pageable);
			} else if(schType.equals("orderDate")) {
				// p = ordersRepository.findByMaterialNameContaining(kwd, pageable);
			}
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			log.info("listPage", e);
		}
		return p;
	}

	@Override
	public void insertOrders(Orders entity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOrders(Orders entity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteOrders(long orderid) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Orders findById(long orderid) {
		// TODO Auto-generated method stub
		return null;
	}
/*
	@Transactional
	@Override
	public void insertMaterial(Material entity, MultipartFile materialPhoto) throws Exception {
		
		// 최종 저장 경로
		String fullPath = uploadPathRoot + File.separator + materialSubPath;
		
		try {
			if(materialPhoto != null && ! materialPhoto.isEmpty()) {
				
				String extension = materialPhoto.getOriginalFilename().substring(materialPhoto.getOriginalFilename().lastIndexOf("."));
				String savedFilename = fileManager.generateUniqueFileName(fullPath, extension);
				
				File dest = new File(fullPath, savedFilename);
				materialPhoto.transferTo(dest);
				
				entity.setMaterialPhoto(savedFilename);	
			}
			
			materialRepository.save(entity);
			
		} catch (Exception e) {
			log.info("insertMaterial: ", e);
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateMaterial(Material entity) throws Exception {
		try {
			materialRepository.save(entity);
		} catch (Exception e) {
			log.info("updateMaterial: ", e);
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void deleteMaterial(long materialid) throws Exception {
		try {
			Material dto = findById(materialid);
			if(dto == null) {
				return;
			}
			
			String fullPath = uploadPathRoot + File.separator + materialSubPath;
			if(dto.getMaterialPhoto() != null) {
				fileManager.deletePath(fullPath + File.separator + dto.getMaterialPhoto());
			}
			
			materialRepository.deleteById(materialid);
		} catch (Exception e) {
			log.info("deleteMaterial: ", e);
			throw e;
		}
	}

	@Override
	public Material findById(long materialid) {
		Material dto = null;
		
		try {
			Optional<Material> material = materialRepository.findById(materialid);
			dto  = material.get();
			
		} catch (NoSuchElementException  e) {
		} catch (Exception e) {
			log.info("findById", e);
		}
		
		return dto;
	}

	*/

}
