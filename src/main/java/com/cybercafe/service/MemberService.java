package com.cybercafe.service;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.cybercafe.model.Member;
import com.cybercafe.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;

    public Optional<Member> findMember(long gid, long memberID) {
        return this.memberRepository.findOne(Example.of(Member.builder().gid(gid).memberId(memberID).build()));
    }

}
