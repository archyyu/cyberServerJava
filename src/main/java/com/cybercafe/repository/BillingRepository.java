package com.cybercafe.repository;

import com.cybercafe.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    
    List<Billing> findByOnlineIDAndMemberIDOrderByBillingIDDesc(Long onlineID, Long memberID);

    Billing findTopByMemberIDAndEndTimeIsNullOrderByBillingIDDesc(Long memberID);

    @Modifying
    @Query("UPDATE Billing b SET b.nextCostTimestamp = :nextCost, b.maxEndTimestamp = :maxEnd WHERE b.billingID = :billingId")
    int updateBillingTimestamps(@Param("billingId") Long billingId, @Param("nextCost") LocalDateTime nextCost, @Param("maxEnd") LocalDateTime maxEnd);
}
