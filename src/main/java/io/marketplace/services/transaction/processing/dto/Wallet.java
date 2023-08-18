package io.marketplace.services.transaction.processing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@Builder
public class Wallet {

    private String walletId;

    private String walletName;

    private String userId;

    private String currencyCode;

    private WalletBankAccount bankAccount;

    private HashMap<String, String> restrictions;

    private LocalDateTime createdAt;

    private String status;

    private String type;

    private BigDecimal availableBalance;

    private BigDecimal currentBalance;
}
