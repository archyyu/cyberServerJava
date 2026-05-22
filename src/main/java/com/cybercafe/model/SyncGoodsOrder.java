package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "sync_goods_order")
public class SyncGoodsOrder {
    @Id
    private Long goodsOrderID;
    private Integer sync;
}
