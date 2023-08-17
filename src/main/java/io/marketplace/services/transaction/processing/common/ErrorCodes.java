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

    // 403

    // 404
    ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR("161.01.404.01", "Requested Configuration Id not found while deleting RC Configuration."),
    // 409

    // 500
    ERROR_WHILE_ADD_CONFIGURATIONS("161.01.500.01", "Error While Adding Configuration."),
    ERR_DELETE_DB_ERROR("161.01.500.02", "Error occurred when deleting Configuration record.");

    private final String code;
    private final String message;
}
