package com.cybercafe.repository;

import com.cybercafe.model.RechargeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RechargeOrderRepository extends JpaRepository<RechargeOrder, Long> {
}
