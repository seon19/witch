package com.sp.app.service;

import java.util.Map;

import com.sp.app.entity.Member;

public interface MemberService {
	
	public Member loginMember(Map<String, Object> map);

	public Member findById(long memberId);
	
	public Member addExp(long memberId, int expGained);

}
