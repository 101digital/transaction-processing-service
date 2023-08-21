package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

/**
 * Specifies the Mutability of the Transaction record.
 */
public enum OBTransactionMutability1Code {

    MUTABLE("Mutable"),

    IMMUTABLE("Immutable");

    private String value;

    OBTransactionMutability1Code(String value) {
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
    public static OBTransactionMutability1Code fromValue(String value) {
        for (OBTransactionMutability1Code b : OBTransactionMutability1Code.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_MUTABILITY_CODE.getCode(),
                  ErrorCodes.INVALID_TRANSACTION_MUTABILITY_CODE.getMessage(),
                  value);
    }
}

