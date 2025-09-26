package com.sp.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Purchase;

public interface UserPurchaseRepository extends JpaRepository<Purchase, Long> {

	Page<Purchase> findAllByIsAvailable(boolean isAvailable, Pageable pageable);
}
