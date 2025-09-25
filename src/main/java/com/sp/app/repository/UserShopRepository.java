package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Shop;

public interface UserShopRepository extends JpaRepository<Shop, Long> {

        Page<Shop> findByIsAvailableTrue(Pageable pageable);
    
    
    

}
