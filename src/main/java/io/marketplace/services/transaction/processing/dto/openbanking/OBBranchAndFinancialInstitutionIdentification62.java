package io.marketplace.services.transaction.processing.dto.openbanking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Financial institution servicing an account for the debtor.
 */
@ApiModel(description = "Financial institution servicing an account for the debtor.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T08:49:53.540+07:00[Asia/Bangkok]")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OBBranchAndFinancialInstitutionIdentification62 {
    @JsonProperty("SchemeName")
    @SerializedName("SchemeName")
    private String schemeName;

    @JsonProperty("Identification")
    @SerializedName("Identification")
    private String identification;

    @JsonProperty("Name")
    @SerializedName("Name")
    private String name;

    @JsonProperty("PostalAddress")
    @SerializedName("PostalAddress")
    private OBPostalAddress6 postalAddress;

    public OBBranchAndFinancialInstitutionIdentification62 schemeName(String schemeName) {
        this.schemeName = schemeName;
        return this;
    }

    /**
     * Name of the identification scheme, in a coded form as published in an external list.
     *
     * @return schemeName
     */
    @ApiModelProperty(value = "Name of the identification scheme, in a coded form as published in an external list.")


    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public OBBranchAndFinancialInstitutionIdentification62 identification(String identification) {
        this.identification = identification;
        return this;
    }

    /**
     * Unique and unambiguous identification of a financial institution or a branch of a financial institution.
     *
     * @return identification
     */
    @ApiModelProperty(value = "Unique and unambiguous identification of a financial institution or a branch of a financial institution.")

    @Size(min = 1, max = 35)
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public OBBranchAndFinancialInstitutionIdentification62 name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name by which an agent is known and which is usually used to identify that agent.
     *
     * @return name
     */
    @ApiModelProperty(value = "Name by which an agent is known and which is usually used to identify that agent.")

    @Size(min = 1, max = 140)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OBBranchAndFinancialInstitutionIdentification62 postalAddress(OBPostalAddress6 postalAddress) {
        this.postalAddress = postalAddress;
        return this;
    }

    /**
     * Get postalAddress
     *
     * @return postalAddress
     */
    @ApiModelProperty(value = "")

    @Valid

    public OBPostalAddress6 getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(OBPostalAddress6 postalAddress) {
        this.postalAddress = postalAddress;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OBBranchAndFinancialInstitutionIdentification62 obBranchAndFinancialInstitutionIdentification62 = (OBBranchAndFinancialInstitutionIdentification62) o;
        return Objects.equals(this.schemeName, obBranchAndFinancialInstitutionIdentification62.schemeName) &&
          Objects.equals(this.identification, obBranchAndFinancialInstitutionIdentification62.identification) &&
          Objects.equals(this.name, obBranchAndFinancialInstitutionIdentification62.name) &&
          Objects.equals(this.postalAddress, obBranchAndFinancialInstitutionIdentification62.postalAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemeName, identification, name, postalAddress);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OBBranchAndFinancialInstitutionIdentification62 {\n");

        sb.append("    schemeName: ").append(toIndentedString(schemeName)).append("\n");
        sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    postalAddress: ").append(toIndentedString(postalAddress)).append("\n");
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

