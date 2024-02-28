package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Set of elements used to fully identify the type of underlying transaction resulting in an entry.
 */
@ApiModel(description = "Set of elements used to fully identify the type of underlying transaction resulting in an entry.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T08:49:53.540+07:00[Asia/Bangkok]")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OBBankTransactionCodeStructure1 {
    @JsonProperty("Code")
    @SerializedName("Code")
    private String code;

    @JsonProperty("SubCode")
    @SerializedName("SubCode")
    private String subCode;

    public OBBankTransactionCodeStructure1 code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Specifies the family within a domain.
     *
     * @return code
     */
    @ApiModelProperty(required = true, value = "Specifies the family within a domain.")
    @NotNull

    @Size(min = 1, max = 4)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OBBankTransactionCodeStructure1 subCode(String subCode) {
        this.subCode = subCode;
        return this;
    }

    /**
     * Specifies the sub-product family within a specific family.
     *
     * @return subCode
     */
    @ApiModelProperty(required = true, value = "Specifies the sub-product family within a specific family.")
    @NotNull

    @Size(min = 1, max = 4)
    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OBBankTransactionCodeStructure1 obBankTransactionCodeStructure1 = (OBBankTransactionCodeStructure1) o;
        return Objects.equals(this.code, obBankTransactionCodeStructure1.code) &&
          Objects.equals(this.subCode, obBankTransactionCodeStructure1.subCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, subCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OBBankTransactionCodeStructure1 {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    subCode: ").append(toIndentedString(subCode)).append("\n");
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

