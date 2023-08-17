package io.marketplace.services.transaction.processing.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
	
	// 404
	ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR("161.01.404.01", "Requested Configuration Id not found while deleting RC Configuration."),
	
	// 500
	ERR_DELETE_DB_ERROR("161.01.500.01", "Error occurred when deleting Configuration record.");
	
	private final String code;
    private final String message;
}
