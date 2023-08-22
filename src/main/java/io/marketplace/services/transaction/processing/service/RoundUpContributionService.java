package io.marketplace.services.transaction.processing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.utils.StringUtils;
import io.marketplace.services.transaction.processing.client.WalletClient;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.dto.Wallet;
import io.marketplace.services.transaction.processing.dto.WalletListResponse;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest.Account;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest.Transaction;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferResponse;
import io.marketplace.services.transaction.processing.dto.openbanking.OBActiveOrHistoricCurrencyAndAmount9;
import io.marketplace.services.transaction.processing.dto.openbanking.OBBankTransactionCodeStructure1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCashAccount61;
import io.marketplace.services.transaction.processing.dto.openbanking.OBCreditDebitCode1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBEntryStatus1Code;
import io.marketplace.services.transaction.processing.dto.openbanking.OBMerchantDetails1;
import io.marketplace.services.transaction.processing.dto.openbanking.OBTransaction6;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;

@Service
public class RoundUpContributionService {
	private static final Logger log = LoggerFactory.getLogger(RoundUpContributionService.class);

	private static final String MERCHANT_CATERGORY_CODE = "merchant_category_code";
	private static final String EMPTY_VALUE = "";
	private final String CONSUMER_CODE = "ADB";
	private final String TRANSACTION_TYPE = "OWN_ACCOUNTS_TRANSFER";

	@Value("#{'${roundUpConfig.eligibleBankTransactionCodes}'.split(',')}")
	private List<String> eligibleBankTransactionCodes;

	@Value("${roundUpConfig.type:ROUNDUP_CONTRIBUTION}")
	private String roundUpConfigType;

	@Value("${transaction-processing-config.contribution-param-name:contributionWalletId}")
	private String contributionWalletId;

	@Autowired
	WalletClient walletClient;

	@Autowired
	private Gson gson;

	@Autowired
	private ConfigurationRepository configurationRepository;

	public void processTransaction(OBTransaction6 transaction) {
		Optional<ConfigurationEntity> optConfig = isTransactionEligible(transaction);
		if (!optConfig.isPresent()) {
			log.info("Transaction not eligible to round up contribution {}", gson.toJson(transaction));
			return;
		}

		log.info("Round up process started {}", gson.toJson(transaction));
		ConfigurationEntity configurationEntity = optConfig.get();
		OBActiveOrHistoricCurrencyAndAmount9 amount = Optional.ofNullable(transaction).map(OBTransaction6::getAmount)
				.orElse(new OBActiveOrHistoricCurrencyAndAmount9());

		BigDecimal roundUpAmount = getRoundUpAmount(configurationEntity, amount.getAmount());

		if (roundUpAmount.compareTo(BigDecimal.ZERO) > 0) {
			WalletFundTransferRequest walletFundTransferRequest = toWalletFundTransferRequest(configurationEntity,
					roundUpAmount, amount.getCurrency());
			log.info("Round up WalletFundTransferRequest: {}", gson.toJson(walletFundTransferRequest));

			WalletFundTransferResponse walletFundTransferResponse = walletClient
					.walletFundTransfer(walletFundTransferRequest, transaction.getUserId());
			log.info("Round up WalletFundTransferResponse: {}", gson.toJson(walletFundTransferResponse));
		}
	}

	private BigDecimal getRoundUpAmount(ConfigurationEntity configurationEntity, String amount) {
		switch (configurationEntity.getLogicCode()) {
		case "DEFAULT":
			return calculateCeilingDifference(amount);
		case "TODO":
			return new BigDecimal("0.00"); // implement another round up logic
		default:
			return calculateCeilingDifference(amount);
		}
	}

	private BigDecimal calculateCeilingDifference(String amount) {
		BigDecimal txnAmount = new BigDecimal(amount);
		BigDecimal ceilingValue = txnAmount.setScale(0, RoundingMode.CEILING);
		return ceilingValue.subtract(txnAmount);
	}

	private Optional<ConfigurationEntity> isTransactionEligible(OBTransaction6 transaction) {
		Optional<ConfigurationEntity> optConfig = Optional.empty();
		log.info("Round up process eligibility check started {}", gson.toJson(transaction));
		if (OBEntryStatus1Code.COMPLETED != transaction.getStatus()
				|| OBCreditDebitCode1.DEBIT != transaction.getCreditDebitIndicator()) {
			log.info("Not Eligible for round up due to Status: {} and CreditDebitIndicator: {} ",
					transaction.getStatus().getValue(), transaction.getCreditDebitIndicator().getValue());
			return optConfig;
		}

		String bankTransactionCode = Optional.ofNullable(transaction).map(OBTransaction6::getBankTransactionCode)
				.map(OBBankTransactionCodeStructure1::getCode).orElse(EMPTY_VALUE);

		if (!eligibleBankTransactionCodes.contains(bankTransactionCode) || !validateMerchantCategoryCode(transaction)) {
			log.info("Not Eligible for round up due to not supported bank transaction code or mcc ");
			return optConfig;
		}

		String debitorAccountNumber = Optional.ofNullable(transaction).map(OBTransaction6::getDebtorAccount)
				.map(OBCashAccount61::getIdentification)
				.orElseThrow(() -> new BadRequestException(ErrorCodes.MISSING_DEBITOR_ACCOUNT.getCode(),
						ErrorCodes.MISSING_DEBITOR_ACCOUNT.getMessage(), gson.toJson(transaction)));
		log.info("Get WalletId By Account Number {}", debitorAccountNumber);
		String walletId = getWalletIdByAccountNumber(debitorAccountNumber);
		log.info("Debitor account walletId {}", walletId);
		if (walletId != null) {
			return configurationRepository.findByTypeAndWallet(roundUpConfigType, walletId);
		}
		return optConfig;
	}

	private boolean validateMerchantCategoryCode(OBTransaction6 transaction) {
		String mcc = Stream
				.of((String) transaction.getSupplementaryData().getOrDefault(MERCHANT_CATERGORY_CODE, EMPTY_VALUE),
						Optional.ofNullable(transaction).map(OBTransaction6::getMerchantDetails)
								.map(OBMerchantDetails1::getMerchantCategoryCode).orElse(EMPTY_VALUE))
				.filter(str -> !str.isEmpty()).findFirst().orElse(EMPTY_VALUE);
		if (StringUtils.isEmpty(mcc) || "0000".equals(mcc)) {
			log.info("Merchant category Code not supported {}", mcc);
			return false;
		}
		return true;
	}

	private String getWalletIdByAccountNumber(String accountNumber) {

		return Optional.ofNullable(walletClient.getWalletInformationByAccountNumber(accountNumber))
				.map(WalletListResponse::getData).stream().flatMap(Collection::stream).findFirst()
				.map(Wallet::getWalletId).orElse(null);
	}

	private WalletFundTransferRequest toWalletFundTransferRequest(ConfigurationEntity configurationEntity,
			BigDecimal roundUpAmount, String currency) {
		String debitorWalletId = configurationEntity.getWallet();
		Optional<ConfigurationParamEntity> configurationParamEntity = configurationEntity.getConfigurationParamList()
				.stream().filter(param -> contributionWalletId.equals(param.getParamName())).findFirst();
		if (!configurationParamEntity.isPresent()) {
			throw new BadRequestException(ErrorCodes.ERR_SAVINGS_POT_NOT_FOUND_ERROR.getCode(),
					ErrorCodes.ERR_SAVINGS_POT_NOT_FOUND_ERROR.getMessage(), "");
		}
		return WalletFundTransferRequest.builder().consumerCode(CONSUMER_CODE).transactionType(TRANSACTION_TYPE)
				.transaction(Transaction.builder().amount(roundUpAmount).currencyCode(currency)
						.creditor(Account.builder()
								.wallet(WalletFundTransferRequest.Wallet.builder()
										.walletId(configurationParamEntity.get().getValue()).build())
								.build())
						.debtor(Account.builder()
								.wallet(WalletFundTransferRequest.Wallet.builder().walletId(debitorWalletId).build())
								.build())
						.build())
				.build();
	}

}
