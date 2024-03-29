package io.marketplace.services.transaction.processing.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

	public static final String NOTIFICATION_DATA_KEY_AMOUNT = "amount";
    public static final String NOTIFICATION_DATA_KEY_CURRENCY = "currency";
    public static final String NOTIFICATION_DATA_KEY_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_DATA_KEY_SAVINGS_POT_NAME = "savingsPotName";

    @UtilityClass
    public static class EventTitle {
        public static final String ADD_CONFIGURATION_REQUEST = "Add Configuration Request";
        public static final String ADD_CONFIGURATION_RESPONSE = "Add Configuration Respone";
        public static final String DELETE_CONFIGURATION_REQUEST = "Delete Configuration Request";
        public static final String DELETE_CONFIGURATION_RESPONSE = "Delete Configuration Respone";
        public static final String WALLET_DATA_RECV_REQUEST = "Received the kafka message for the wallet-data-changed.";
        public static final String WALLET_DATA_RECV_RESPONSE = "Successfully processed the kafka message for the wallet-data-changed.";
        public static final String TRANSACTION_DATA_RECV_REQUEST = "Received the kafka message for the transaction-data-changed.";
        public static final String TRANSACTION_DATA_RECV_RESPONSE = "Successfully processed the kafka message for the transaction-data-changed";
        public static final String INVOKE_WALLET_BY_ACCOUNT_NUMBER = "Invoke wallet service for retrive wallet id by account number";
        public static final String RESPONSE_WALLET_BY_ACCOUNT_NUMBER = "Getting wallet info response by account number";
        


    }


    @UtilityClass
    public static class UseCase {

        public static final String ADD_CONFIGURATION = "Add Configuration";
        public static final String DELETE_CONFIGURATION = "Delete Configuration";
        public static final String ACTIVITY_RECEIVE_TRANSACTION_DATA = "Transaction data received for round up contribution";
        public static final String RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA = "Receive Wallet data changed from Kafka";
        public static final String GET_CONFIGURATIONS = "Get Configurations";
        public static final String UPDATE_CONFIGURATIONS = "Update Configurations";
        public static final String WALLET_FUND_TRANSFER = "Wallet Fund Transfer";
        public static final String GET_WALLET_DETAILS = "Get Wallet Details";

    }

    @UtilityClass
    public static class  EventCode {
        public static final String SEQUENCE_INVOKE = ".INVOKE";
        public static final String SEQUENCE_RESPONSE = ".RESPONSE";
        public static final String SEQUENCE_REQUEST = ".REQUEST";
        public static final String KAFKA_PUSH_SUCCESS = ".KAFKA-PUSH-SUCCESS";
        public static final String KAFKA_PUSH_FAILED = ".KAFKA-PUSH-FAILED";

        public static final String ADD_CONFIGURATION_EVENT_CODE = "POST.CONFIGURATION";
        public static final String DELETE_CONFIGURATION_EVENT_CODE = "DELETE.CONFIGURATION";
        
        public static final String WALLET_CLOSED = "WALLET.CLOSED";
        public static final String EVENT_RECEIVE_TRANSACTION_DATA = "RECV.TRANSACTION.DATA";
        public static final String RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA = "RECV.WALLET.DATA";
        public static final String GET_CONFIGURATIONS = "GET.CONFIGURATIONS";
        public static final String PUT_CONFIGURATIONS = "PUT.CONFIGURATIONS";
        public static final String WALLET_FUND_TRANSFER = "POST.WALLET.FUND.TRANSFER";
        public static final String GET_WALLET_ACCOUNT = "GET.WALLET.ACCOUNT";


    }

    @UtilityClass
    public static final class Permissions {

        public static final String ADD_CONFIGURATION_PERMISSION = "POST_CONFIGURATION";
        public static final String DELETE_CONFIGURATION_PERMISSION = "DELETE_CONFIGURATION";

    }
}
