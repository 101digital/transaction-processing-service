package io.marketplace.services.transaction.processing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestSearchConfiguration {
    private String walletId;
    private String type;
    private Integer pageNumber;
    private Integer pageSize;
    private String listOrders;
}
