package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

/**
 * Balance type, in a coded form.
 */
public enum OBBalanceType1Code {

    CLOSINGAVAILABLE("ClosingAvailable"),

    CLOSINGBOOKED("ClosingBooked"),

    CLOSINGCLEARED("ClosingCleared"),

    EXPECTED("Expected"),

    FORWARDAVAILABLE("ForwardAvailable"),

    INFORMATION("Information"),

    INTERIMAVAILABLE("InterimAvailable"),

    INTERIMBOOKED("InterimBooked"),

    INTERIMCLEARED("InterimCleared"),

    OPENINGAVAILABLE("OpeningAvailable"),

    OPENINGBOOKED("OpeningBooked"),

    OPENINGCLEARED("OpeningCleared"),

    PREVIOUSLYCLOSEDBOOKED("PreviouslyClosedBooked"),

    INTERIMTOTAL("InterimTotal"),

    HOLDBALANCE("HoldBalance"),

    LOCKEDBALANCE("LockedBalance"),

    BLOCKEDBALANCE("BlockedBalance");

    private final String value;

    OBBalanceType1Code(String value) {
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
    public static OBBalanceType1Code fromValue(String value) {
        for (OBBalanceType1Code b : OBBalanceType1Code.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_BALANCE_TYPE.getCode(),
                ErrorCodes.INVALID_TRANSACTION_BALANCE_TYPE.getMessage(),
                value);
    }
}
