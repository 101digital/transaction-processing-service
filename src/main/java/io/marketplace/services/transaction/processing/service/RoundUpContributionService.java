package io.marketplace.services.transaction.processing.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.marketplace.commons.utils.StringUtils;
import io.marketplace.services.transaction.processing.dto.openbanking.OBBankTransactionCodeStructure1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCashAccount61;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCreditDebitCode1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBEntryStatus1Code;
import io.marketplace.services.transaction.processing.dto.openbanking.OBMerchantDetails1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBTransaction6;

@Service
public class RoundUpContributionService {

	private static final String MERCHANT_CATERGORY_CODE = "merchant_category_code";
	private static final String EMPTY_VALUE = "";

	@Value("#{'${roundUpConfig.eligibleBankTransactionCodes}'.split(',')}")
	private List<String> eligibleBankTransactionCodes;

	public void processTransaction(OBTransaction6 transaction) {

		String debitorAccountNumber = Optional.ofNullable(transaction).map(OBTransaction6::getDebtorAccount)
				.map(OBCashAccount61::getIdentification).orElseThrow();

	}

	private boolean isTransactionEligible(OBTransaction6 transaction) {

		if (OBEntryStatus1Code.COMPLETED != transaction.getStatus()) {
			return false;
		}

		if (OBCreditDebitCode1.DEBIT != transaction.getCreditDebitIndicator()) {
			return false;
		}

		String banckTransactionCode = Optional.ofNullable(transaction).map(OBTransaction6::getBankTransactionCode)
				.map(OBBankTransactionCodeStructure1::getCode).orElse(EMPTY_VALUE);

		if (!eligibleBankTransactionCodes.contains(banckTransactionCode)) {
			return false;
		}
		
		if(!validateMerchantCategoryCode(transaction)) {
			return false;
		}

		return true;
	}

	private boolean validateMerchantCategoryCode(OBTransaction6 transaction) {
		String mcc = Stream
				.of((String) transaction.getSupplementaryData().getOrDefault(MERCHANT_CATERGORY_CODE, EMPTY_VALUE),
						Optional.ofNullable(transaction).map(OBTransaction6::getMerchantDetails)
								.map(OBMerchantDetails1::getMerchantCategoryCode).orElse(EMPTY_VALUE))
				.filter(str -> !str.isEmpty()).findFirst().orElse(EMPTY_VALUE);
		if(StringUtils.isEmpty(mcc) || "0000".equals(mcc)) {
			return false;
		}
		return true;
	}
	
	private 

}
