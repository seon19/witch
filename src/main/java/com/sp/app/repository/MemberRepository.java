package com.sp.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sp.app.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	
    public Optional<Member> findByUserIdAndPassword(String userId, String password);
}