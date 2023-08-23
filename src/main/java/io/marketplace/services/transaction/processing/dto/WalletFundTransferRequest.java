package io.marketplace.services.transaction.processing.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletFundTransferRequest {

    private String transactionType;
    private String consumerCode;
    private Transaction transaction;
    private List<CustomField> customFields;

    @Data
    @Builder
    public static class Transaction {
        private BigDecimal amount;
        private String currencyCode;
        private Account creditor;
        private Account debtor;
    }

    @Data
    @Builder
    public static class Account {
        private Wallet wallet;
    }

    @Data
    @Builder
    public static class Wallet {
        private String walletId;
    }
    
    @Data
    @Builder
    public static class CustomField {
    	private String key;
        private String value;

    }
}
