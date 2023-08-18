package io.marketplace.services.transaction.processing.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {


    @UtilityClass
    public static class EventTitle {
        public static final String ADD_CONFIGURATION_REQUEST = "Add Configuration Request";
        public static final String ADD_CONFIGURATION_RESPONSE = "Add Configuration Respone";
        public static final String DELETE_CONFIGURATION_REQUEST = "Delete Configuration Request";
        public static final String DELETE_CONFIGURATION_RESPONSE = "Delete Configuration Respone";

    }


    @UtilityClass
    public static class UseCase {

        public static final String ADD_CONFIGURATION = "Add Configuration";
        public static final String DELETE_CONFIGURATION = "Delete Configuration";
        public static final String RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA = "Receive Wallet data changed from Kafka";

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
        public static final String RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA = "RECEIVE.WALLET.DATA.CHANGED.FROM.KAFKA";


    }

    @UtilityClass
    public static final class Permissions {

        public static final String ADD_CONFIGURATION_PERMISSION = "POST_CONFIGURATION";
        public static final String DELETE_CONFIGURATION_PERMISSION = "DELETE_CONFIGURATION";

    }
}
