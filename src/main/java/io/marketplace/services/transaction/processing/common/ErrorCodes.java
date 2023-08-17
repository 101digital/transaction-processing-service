package io.marketplace.services.transaction.processing.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    //400
    MISSING_CONFIGURATION_TYPE("000.01.400.01","Missing Configuration Type."),
    MISSING_CONFIGURATION_LOGIC_CODE("000.01.400.02","Missing Configuration Logic Code."),
    INVALID_CONFIGURATION_LOGIC_CODE("000.01.400.03","Invalid Configuration Logic Code."),
    INVALID_CONFIGURATION_TYPE("000.01.400.04","Invalid Configuration Type."),
    ALREADY_CONFIGURATION_CREATED("000.01.400.05","Configuration is already created"),

    //403

    // 404

    //409

    //500
    ERROR_WHILE_ADD_CONFIGURATIONS("000.01.500.01","Error While Adding Configuration.");

    private final String code;
    private final String message;
}
