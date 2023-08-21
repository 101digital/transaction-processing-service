package io.marketplace.services.transaction.processing.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.utils.StringUtils;
import io.marketplace.services.transaction.processing.client.WalletClient;
import io.marketplace.services.transaction.processing.dto.Wallet;
import io.marketplace.services.transaction.processing.dto.WalletListResponse;
import io.marketplace.services.transaction.processing.dto.openbanking.OBBankTransactionCodeStructure1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCashAccount61;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCreditDebitCode1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBEntryStatus1Code;
import io.marketplace.services.transaction.processing.dto.openbanking.OBMerchantDetails1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBTransaction6;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;

@Service
public class RoundUpContributionService {
	private static final Logger log = LoggerFactory.getLogger(RoundUpContributionService.class);
	
	private static final String MERCHANT_CATERGORY_CODE = "merchant_category_code";
	private static final String EMPTY_VALUE = "";

	@Value("#{'${roundUpConfig.eligibleBankTransactionCodes}'.split(',')}")
	private List<String> eligibleBankTransactionCodes;
	
	@Value("${roundUpConfig.type:ROUNDUP_CONTRIBUTION}")
    private String roundUpConfigType;

	@Autowired
	WalletClient walletClient;
	
	@Autowired private Gson gson;
	
	@Autowired private ConfigurationRepository configurationRepository;

	public void processTransaction(OBTransaction6 transaction) {
		if(!isTransactionEligible(transaction)) {
			log.info("Transaction not supported to round up configuration {}", gson.toJson(transaction));
			return;
		}
		log.info("Transaction supported to round up configuration {}", gson.toJson(transaction));
	}
	
	private boolean isTransactionEligible(OBTransaction6 transaction) {
	    if (OBEntryStatus1Code.COMPLETED != transaction.getStatus() ||
	        OBCreditDebitCode1.DEBIT != transaction.getCreditDebitIndicator()) {
	        return false;
	    }

	    String bankTransactionCode = Optional.ofNullable(transaction)
	            .map(OBTransaction6::getBankTransactionCode)
	            .map(OBBankTransactionCodeStructure1::getCode)
	            .orElse(EMPTY_VALUE);

	    if (!eligibleBankTransactionCodes.contains(bankTransactionCode) ||
	        !validateMerchantCategoryCode(transaction) ||
	        !validateConfiguration(transaction)) {
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
		if (StringUtils.isEmpty(mcc) || "0000".equals(mcc)) {
			return false;
		}
		return true;
	}

	private boolean validateConfiguration(OBTransaction6 transaction) {
		String debitorAccountNumber = Optional.ofNullable(transaction).map(OBTransaction6::getDebtorAccount)
				.map(OBCashAccount61::getIdentification).orElseThrow();

		String walletId = getWalletIdByAccountNumber(debitorAccountNumber);
		
		if(walletId != null) {
			Optional<ConfigurationEntity> optConfig = configurationRepository.findByTypeAndWallet(roundUpConfigType, walletId);
			if(optConfig.isPresent()) {
				return true;
			}
		}
		return false;
	}
	
	private String getWalletIdByAccountNumber(String accountNumber) {

		return Optional.ofNullable(walletClient.getWalletInformationByAccountNumber(accountNumber))
				.map(WalletListResponse::getData).stream().flatMap(Collection::stream).findFirst()
				.map(Wallet::getWalletId).orElse(null);
	}

}
