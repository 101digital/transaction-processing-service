package io.marketplace.services.transaction.processing.client.dto;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.apache.commons.collections.CollectionUtils;

import com.google.gson.annotations.SerializedName;

import io.marketplace.commons.model.dto.CommonSearchRequestDto;
import io.marketplace.commons.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Validated
@Valid
public class RequestSearchWalletDto extends CommonSearchRequestDto {

    @ApiModelProperty(value = "The user identity, that is a owner of this wallet", required = false, dataType = "UUID", example = "63aa9e5f-3277-49e0-8a8d-5d80689f26ec", position = 1)
    @Size(max = 36)
    private String userId;

    @ApiModelProperty(value = "The organization identity, that is a company of this wallet", required = false, dataType = "UUID", example = "63aa9e5f-3277-49e0-8a8d-5d80689f26ec", position = 1)
    @Size(max = 36)
    private String orgId;

    @ApiModelProperty(value = "Type of the wallet to be updated. Only fixed values are allowed", required = false, dataType = "String", example = "BANK_WALLET", position = 4)
    @SerializedName("type")
    private String type;

    @ApiModelProperty(value = "Bank account number")
    @SerializedName("accountNumber")
    private String accountNumber;

    @ApiModelProperty(value = "Bank account id")
    @SerializedName("accountId")
    private String accountId;
    
    @ApiModelProperty(value = "Wallet id")
    @SerializedName("walletId")
    private String walletId;
    
    @ApiModelProperty(value = "Wallet status")
    @SerializedName("status")
    private WalletStatus status;

    @ApiModelProperty(value = "Bank id", example = "PDAX")
    @SerializedName("bankId")
    private String bankId;
    
    @ApiModelProperty(value = "List of Product identifiers to be used in search", example = "1144a74f-dac2-4db9-ac6a-5ff2e4abd784")
    @SerializedName("productIds")
    private List<String> productIds;
    
    @ApiModelProperty(value = "List of Bank account types.", example = "SavingsAccount")
    @SerializedName("accountTypes")
    private List<String> accountTypes;
    
    @ApiModelProperty(value = "List of Bank account's sub type.", example = "")
    @SerializedName("accountSubTypes")
    private List<String> accountSubTypes;
    
    @ApiModelProperty(value = "Set to true in case we want to fetch the closed accounts as well, the default value is false", example = "false")
    private Boolean includeClosedAccounts;


    public MultiValueMap<String, String> getQueryParams() {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


        if (StringUtils.isNotEmpty(userId)) {
            queryParams.add("userId", userId);
        }

        if (StringUtils.isNotEmpty(orgId)) {
            queryParams.add("orgId", orgId);
        }

        if (StringUtils.isNotEmpty(type)) {
            queryParams.add("type", type);
        }

        if (StringUtils.isNotEmpty(accountNumber)) {
            queryParams.add("accountNumber", accountNumber);
        }

        if (StringUtils.isNotEmpty(accountId)) {
            queryParams.add("accountId", accountId);
        }

        if (StringUtils.isNotEmpty(walletId)) {
            queryParams.add("walletId", walletId);
        }

        if (Optional.ofNullable(status).isPresent()) {
            queryParams.add("status", status.toString());
        }

        if (Optional.ofNullable(bankId).isPresent()) {
            queryParams.add("bankId", bankId);
        }

        if (CollectionUtils.isNotEmpty(productIds)) {
            queryParams.add("productIds", String.join(",", productIds));
        }

        if (CollectionUtils.isNotEmpty(accountTypes)) {
            queryParams.add("accountTypes", String.join(",", accountTypes));
        }

        if (CollectionUtils.isNotEmpty(accountSubTypes)) {
            queryParams.add("accountSubTypes", String.join(",", accountSubTypes));
        }

        if (StringUtils.isNotEmpty(getSearchText())) {
            queryParams.add("searchText", getSearchText());
        }

        if (Optional.ofNullable(getPageSize()).isPresent()) {
            queryParams.add("pageSize", getPageSize().toString());
        }

        if (Optional.ofNullable(getPageNumber()).isPresent()) {
            queryParams.add("pageNumber", getPageNumber().toString());
        }

        if (StringUtils.isNotEmpty(getListOrders())) {
            queryParams.add("listOrders", getListOrders());
        }

        return queryParams;

    }
}
