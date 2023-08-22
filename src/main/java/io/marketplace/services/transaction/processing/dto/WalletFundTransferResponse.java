package io.marketplace.services.transaction.processing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletFundTransferResponse {

    public String transactionId;
    public TransactionStatus transactionStatus;

    @Data
    @Builder
    public static class TransactionStatus {
        public String status;
    }
}
