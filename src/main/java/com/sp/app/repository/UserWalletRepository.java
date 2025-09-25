package com.sp.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Member;
import com.sp.app.entity.UserWallet;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    List<UserWallet> findByMemberOrderByTransactionDateDesc(Member member);
}