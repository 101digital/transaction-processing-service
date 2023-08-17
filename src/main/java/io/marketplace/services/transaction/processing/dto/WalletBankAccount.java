package io.marketplace.services.transaction.processing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletBankAccount {

    private String bankCode;

    private String accountHolderName;

    private String accountId;

    private String productId;

    private String accountType;

    private String bankCustomerId;

    private String accountNumber;

    private Wallet wallet;
}
