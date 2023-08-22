package io.marketplace.services.transaction.processing.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // 400
    MISSING_CONFIGURATION_TYPE("161.01.400.01", "Missing Configuration Type."),
    MISSING_CONFIGURATION_LOGIC_CODE("161.01.400.02", "Missing Configuration Logic Code."),
    INVALID_CONFIGURATION_LOGIC_CODE("161.01.400.03", "Invalid Configuration Logic Code."),
    INVALID_CONFIGURATION_TYPE("161.01.400.04", "Invalid Configuration Type."),
    ALREADY_CONFIGURATION_CREATED("161.01.400.05", "Configuration is already created"),
    INVALID_TRANSACTION_ADDRESS_TYPE("161.01.400.06", "Invalid transaction address type"),
    INVALID_TRANSACTION_BALANCE_TYPE("161.01.400.07", "Invalid balance type for transaction"),
    INVALID_TRANSACTION_CREDIT_DEBIT_CODE1("161.01.400.08", "Invalid credit debit code 1 for transaction"),
    INVALID_TRANSACTION_CREDIT_DEBIT_CODE2("161.01.400.09", "Invalid credit debit code 2 for transaction"),
    INVALID_TRANSACTION_ENTRY_STATUS_CODE("161.01.400.10", "Invalid entry status code for transaction"),
    INVALID_TRANSACTION_CARD_SCHEME_NAME("161.01.400.11", "Invalid card scheme name for transaction"),
    INVALID_TRANSACTION_AUTHORIZATION_TYPE("161.01.400.12", "Invalid authorization type for transaction"),
    INVALID_TRANSACTION_MUTABILITY_CODE("161.01.400.13", "Invalid mutability code for transaction"),
    MISSING_WALLET_ID("161.01.400.06", "Missing WalletId"),
    MISSING_DEBITOR_ACCOUNT("161.01.400.07", "Missing debitor identification in transaction data change event"),

    // 403

    // 404
    ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR("161.01.404.01", "Requested Configuration Id not found while deleting RC Configuration."),
    // 409

    // 500
    ERROR_WHILE_ADD_CONFIGURATIONS("161.01.500.01", "Error While Adding Configuration."),
    ERR_DELETE_DB_ERROR("161.01.500.02", "Error occurred when deleting Configuration record."),
    ERROR_WHILE_GET_CONFIGURATIONS("161.01.500.03", "Error while getting configurations"),
    ERROR_GETTING_RESPONSE("161.01.500.04", "Error while getting response"),
    ERROR_CALL_WALLET_SERVICE("161.01.500.05","Error While Calling Wallet Service"),
    ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER("161.01.500.06","Error While Calling Wallet Service for retrive walletId by account number");

    private final String code;
    private final String message;
}
