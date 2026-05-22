package com.cybercafe.repository;

import com.cybercafe.model.Online;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnlineRepository extends JpaRepository<Online, Long> {
    
    Online findByMemberIDAndOffLineTimeIsNull(Long memberID);
    
}
