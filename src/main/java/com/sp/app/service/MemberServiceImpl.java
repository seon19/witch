package com.sp.app.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.entity.Member;
import com.sp.app.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member loginMember(Map<String, Object> map) {
        String userId = (String) map.get("userId");
        String password = (String) map.get("password");

        Optional<Member> result = memberRepository.findByIdAndPassword(userId, password);
        return result.orElse(null);
    }
}
