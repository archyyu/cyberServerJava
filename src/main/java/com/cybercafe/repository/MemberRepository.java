package com.cybercafe.repository;

import com.cybercafe.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Member findByAccount(String account);
    
    @Query("SELECT m FROM Member m WHERE m.memberName LIKE %:query% OR m.account LIKE %:query% OR m.phone LIKE %:query%")
    List<Member> fuzzyQueryMember(@Param("query") String query);
}
