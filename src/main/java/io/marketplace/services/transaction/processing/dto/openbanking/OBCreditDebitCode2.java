package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

/**
 * Indicates whether the balance is a credit or a debit balance. Usage: A zero balance is considered to be a credit balance.
 */
public enum OBCreditDebitCode2 {
  
  CREDIT("Credit"),
  
  DEBIT("Debit");

  private String value;

  OBCreditDebitCode2(String value) {
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
  public static OBCreditDebitCode2 fromValue(String value) {
    for (OBCreditDebitCode2 b : OBCreditDebitCode2.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_CREDIT_DEBIT_CODE2.getCode(),
            ErrorCodes.INVALID_TRANSACTION_CREDIT_DEBIT_CODE2.getMessage(),
            value);
  }
}

