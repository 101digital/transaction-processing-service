package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

import java.io.IOException;

/**
 * Indicates whether the transaction is a credit or a debit entry.
 */
@JsonAdapter(OBCreditDebitCode1.Adapter.class)
public enum OBCreditDebitCode1 {

    CREDIT("Credit"),

    DEBIT("Debit");

    private String value;

    OBCreditDebitCode1(String value) {
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
    public static OBCreditDebitCode1 fromValue(String value) {
        for (OBCreditDebitCode1 b : OBCreditDebitCode1.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_CREDIT_DEBIT_CODE1.getCode(),
                ErrorCodes.INVALID_TRANSACTION_CREDIT_DEBIT_CODE1.getMessage(),
                value);
    }

    public static class Adapter extends TypeAdapter<OBCreditDebitCode1> {
        @Override
        public void write(final JsonWriter jsonWriter, final OBCreditDebitCode1 enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public OBCreditDebitCode1 read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return OBCreditDebitCode1.fromValue(value);
        }
    }
}
