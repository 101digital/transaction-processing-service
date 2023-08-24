package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;

/**
 * Identifies the nature of the postal address.
 */
public enum OBAddressTypeCode {
  
  BUSINESS("Business"),
  
  CORRESPONDENCE("Correspondence"),
  
  DELIVERYTO("DeliveryTo"),
  
  MAILTO("MailTo"),
  
  POBOX("POBox"),
  
  POSTAL("Postal"),
  
  RESIDENTIAL("Residential"),
  
  STATEMENT("Statement");

  private String value;

  OBAddressTypeCode(String value) {
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
  public static OBAddressTypeCode fromValue(String value) {
    for (OBAddressTypeCode b : OBAddressTypeCode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_ADDRESS_TYPE.getCode(),
            ErrorCodes.INVALID_TRANSACTION_ADDRESS_TYPE.getMessage(),
            value);
  }
}

