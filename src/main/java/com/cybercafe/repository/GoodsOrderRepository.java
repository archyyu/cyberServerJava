package com.cybercafe.repository;

import com.cybercafe.model.GoodsOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsOrderRepository extends JpaRepository<GoodsOrder, Long> {
}
