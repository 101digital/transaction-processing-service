package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

/**
 * Status of a transaction entry on the books of the account servicer.
 */
public enum OBEntryStatus1Code {

    BOOKED("Booked"),

    PENDING("Pending"),
    
    REJECTED("Rejected"),

    SUBMITTED("Submitted"),

    COMPLETED("Completed"),

    FAILED("Failed");

    private String value;

    OBEntryStatus1Code(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static OBEntryStatus1Code fromValue(String value) {
        for (OBEntryStatus1Code b : OBEntryStatus1Code.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_ENTRY_STATUS_CODE.getCode(),
                ErrorCodes.INVALID_TRANSACTION_ENTRY_STATUS_CODE.getMessage(),
                value);
    }
}

