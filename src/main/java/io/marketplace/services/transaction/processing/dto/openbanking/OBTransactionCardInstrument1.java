package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Set of elements to describe the card instrument used in the transaction.
 */
@ApiModel(description = "Set of elements to describe the card instrument used in the transaction.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T08:49:53.540+07:00[Asia/Bangkok]")

public class OBTransactionCardInstrument1 {
    /**
     * Name of the card scheme.
     */
    public enum CardSchemeNameEnum {
        AMERICANEXPRESS("AmericanExpress"),

        DINERS("Diners"),

        DISCOVER("Discover"),

        MASTERCARD("MasterCard"),

        VISA("VISA");

        private String value;

        CardSchemeNameEnum(String value) {
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
        public static CardSchemeNameEnum fromValue(String value) {
            for (CardSchemeNameEnum b : CardSchemeNameEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_CARD_SCHEME_NAME.getCode(),
                    ErrorCodes.INVALID_TRANSACTION_CARD_SCHEME_NAME.getMessage(),
                    value);
        }
    }

    @JsonProperty("CardSchemeName")
    @SerializedName("CardSchemeName")
    private CardSchemeNameEnum cardSchemeName;

    /**
     * The card authorisation type.
     */
    public enum AuthorisationTypeEnum {
        CONSUMERDEVICE("ConsumerDevice"),

        CONTACTLESS("Contactless"),

        NONE("None"),

        PIN("PIN");

        private String value;

        AuthorisationTypeEnum(String value) {
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
        public static AuthorisationTypeEnum fromValue(String value) {
            for (AuthorisationTypeEnum b : AuthorisationTypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new BadRequestException(ErrorCodes.INVALID_TRANSACTION_AUTHORIZATION_TYPE.getCode(),
                    ErrorCodes.INVALID_TRANSACTION_AUTHORIZATION_TYPE.getMessage(),
                    value);
        }
    }

    @JsonProperty("AuthorisationType")
    @SerializedName("AuthorisationType")
    private AuthorisationTypeEnum authorisationType;

    @JsonProperty("Name")
    @SerializedName("Name")
    private String name;

    @JsonProperty("Identification")
    @SerializedName("Identification")
    private String identification;

    public OBTransactionCardInstrument1 cardSchemeName(CardSchemeNameEnum cardSchemeName) {
        this.cardSchemeName = cardSchemeName;
        return this;
    }

    /**
     * Name of the card scheme.
     *
     * @return cardSchemeName
     */
    @ApiModelProperty(required = true, value = "Name of the card scheme.")
    @NotNull


    public CardSchemeNameEnum getCardSchemeName() {
        return cardSchemeName;
    }

    public void setCardSchemeName(CardSchemeNameEnum cardSchemeName) {
        this.cardSchemeName = cardSchemeName;
    }

    public OBTransactionCardInstrument1 authorisationType(AuthorisationTypeEnum authorisationType) {
        this.authorisationType = authorisationType;
        return this;
    }

    /**
     * The card authorisation type.
     *
     * @return authorisationType
     */
    @ApiModelProperty(value = "The card authorisation type.")


    public AuthorisationTypeEnum getAuthorisationType() {
        return authorisationType;
    }

    public void setAuthorisationType(AuthorisationTypeEnum authorisationType) {
        this.authorisationType = authorisationType;
    }

    public OBTransactionCardInstrument1 name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the cardholder using the card instrument.
     *
     * @return name
     */
    @ApiModelProperty(value = "Name of the cardholder using the card instrument.")

    @Size(min = 1, max = 70)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OBTransactionCardInstrument1 identification(String identification) {
        this.identification = identification;
        return this;
    }

    /**
     * Identification assigned by an institution to identify the card instrument used in the transaction. This identification is known by the account owner, and may be masked.
     *
     * @return identification
     */
    @ApiModelProperty(value = "Identification assigned by an institution to identify the card instrument used in the transaction. This identification is known by the account owner, and may be masked.")

    @Size(min = 1, max = 34)
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OBTransactionCardInstrument1 obTransactionCardInstrument1 = (OBTransactionCardInstrument1) o;
        return Objects.equals(this.cardSchemeName, obTransactionCardInstrument1.cardSchemeName) &&
          Objects.equals(this.authorisationType, obTransactionCardInstrument1.authorisationType) &&
          Objects.equals(this.name, obTransactionCardInstrument1.name) &&
          Objects.equals(this.identification, obTransactionCardInstrument1.identification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSchemeName, authorisationType, name, identification);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OBTransactionCardInstrument1 {\n");

        sb.append("    cardSchemeName: ").append(toIndentedString(cardSchemeName)).append("\n");
        sb.append("    authorisationType: ").append(toIndentedString(authorisationType)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

