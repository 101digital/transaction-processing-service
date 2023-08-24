package io.marketplace.services.transaction.processing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import com.google.gson.Gson;

import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.utils.StringUtils;
import io.marketplace.services.transaction.processing.client.WalletClient;
import io.marketplace.services.transaction.processing.client.dto.RequestSearchWalletDto;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.dto.RoundUpNotificationDetails;
import io.marketplace.services.transaction.processing.dto.Wallet;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest.Account;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest.CustomField;
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
import io.marketplace.services.transaction.processing.utils.EventTrackingService;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;

import static io.marketplace.services.transaction.processing.utils.Constants.NOTIFICATION_DATA_KEY_AMOUNT;
import static io.marketplace.services.transaction.processing.utils.Constants.NOTIFICATION_DATA_KEY_CURRENCY;
import static io.marketplace.services.transaction.processing.utils.Constants.NOTIFICATION_DATA_KEY_SAVINGS_POT_NAME;
import static io.marketplace.services.transaction.processing.utils.Constants.NOTIFICATION_DATA_KEY_TIMESTAMP;
import static io.marketplace.services.transaction.processing.utils.FormatterUtil.formatTimestamp;
import static io.marketplace.services.transaction.processing.utils.FormatterUtil.formatToTwoDecimals;

@Service
public class RoundUpContributionService {
	private static final Logger log = LoggerFactory.getLogger(RoundUpContributionService.class);

	private static final String MERCHANT_CATERGORY_CODE = "merchant_category_code";
	private static final String EMPTY_VALUE = "";
	private final String CONSUMER_CODE = "ADB";
	private final String TRANSACTION_TYPE = "OWN_ACCOUNTS_TRANSFER";
	private final String ROUNDUP_SOURCE_TXN_ID = "ROUNDUP_SOURCE_TXN_ID";
	private final String INSUFFICIENT_BALANCE_ERROR_MESSAGE = "WITHDRAWAL_PAST_OVERDRAFT_CONSTRAINTS";

	@Value("#{'${roundUpConfig.eligibleBankTransactionCodes}'.split(',')}")
	private List<String> eligibleBankTransactionCodes;

	@Value("${roundUpConfig.type:ROUNDUP_CONTRIBUTION}")
	private String roundUpConfigType;

	@Value("${transaction-processing-config.contribution-param-name:contributionWalletId}")
	private String contributionWalletId;
	
	@Value(
            "${transaction-processing-config.notification.roundoff-contribution-success:roundoff-contribution-success}")
    private String roundoffContributionSuccess;

    @Value(
            "${transaction-processing-config.notification.roundoff-contribution-failed:roundoff-contribution-failed}")
    private String roundoffContributionFailed;

    @Value(
        "${transaction-processing-config.notification.roundoff-contribution-insufficient-balance-failed:roundoff-contribution-insufficient-balance-failed}")
    private String roundoffContributionInsufficientBalanceFailed;

    @Value("${transaction-processing-config.notification.timezone:Asia/Kuala_Lumpur}")
    private String notificationTimezone;

	@Autowired
	WalletClient walletClient;

	@Autowired
	private Gson gson;

	@Autowired
	private ConfigurationRepository configurationRepository;
	
	@Autowired private EventTrackingService eventTrackingService;
	
	@Autowired private NotificationService notificationService;
	
	@Transactional
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
		String businessId = String.format("Transaction Id: %s", transaction.getTransactionId());
		if (roundUpAmount.compareTo(BigDecimal.ZERO) > 0) {
			String savingsPotName = getSavingsPotName(configurationEntity);
			RoundUpNotificationDetails roundUpNotificationDetails = RoundUpNotificationDetails.builder()
					.savingsPotName(savingsPotName)
					.currency(amount.getCurrency())
					.amount(roundUpAmount)
					.userId(transaction.getUserId())
					.templateName(roundoffContributionSuccess)
					.build();
			 try {
				WalletFundTransferRequest walletFundTransferRequest = toWalletFundTransferRequest(configurationEntity,
						roundUpAmount, amount.getCurrency());
				CustomField customField = CustomField.builder()
						.key(ROUNDUP_SOURCE_TXN_ID)
						.value(transaction.getTransactionId())
						.build();
				walletFundTransferRequest.setCustomFields(Collections.singletonList(customField));
				log.info("Round up WalletFundTransferRequest: {}", gson.toJson(walletFundTransferRequest));
	
				WalletFundTransferResponse walletFundTransferResponse = walletClient
						.walletFundTransfer(walletFundTransferRequest, transaction.getUserId(),UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA,EventCode.EVENT_RECEIVE_TRANSACTION_DATA);
				log.info("Round up WalletFundTransferResponse: {}", gson.toJson(walletFundTransferResponse));
				sendPaymentNotification(roundUpNotificationDetails);
			}catch (HttpStatusCodeException httpStatusCodeException) {
				log.error(ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getMessage(), httpStatusCodeException);
	            eventTrackingService.traceError(
	                    UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA,
	                    EventCode.EVENT_RECEIVE_TRANSACTION_DATA,
	                    ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getCode(),
	                    ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getMessage(),
	                    businessId,
	                    httpStatusCodeException);
	            sendFailedPaymentNotification(httpStatusCodeException.getResponseBodyAsString(),roundUpNotificationDetails);
			} catch (Exception exception) {
				log.error(ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getMessage(), exception);
				eventTrackingService.traceError(
	                    UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA,
	                    EventCode.EVENT_RECEIVE_TRANSACTION_DATA,
	                    ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getCode(),
	                    ErrorCodes.ERROR_WHILE_TRANSFER_PAYMENT.getMessage(),
	                    businessId,
	                    exception);
				sendFailedPaymentNotification(exception.getMessage(),roundUpNotificationDetails);
			}
		}
	}
	
	private void sendFailedPaymentNotification(String errorMsg, RoundUpNotificationDetails roundUpNotificationDetails) {
		if (errorMsg.contains(INSUFFICIENT_BALANCE_ERROR_MESSAGE)){
			roundUpNotificationDetails.setTemplateName(roundoffContributionInsufficientBalanceFailed);
			sendPaymentNotification(roundUpNotificationDetails);
		}else {
			roundUpNotificationDetails.setTemplateName(roundoffContributionFailed);
			sendPaymentNotification(roundUpNotificationDetails);
		}
	}
	
	private void sendPaymentNotification(RoundUpNotificationDetails roundUpNotificationDetails) {
		notificationService.sendNotification(
                Map.of(
                        NOTIFICATION_DATA_KEY_AMOUNT,
                        formatToTwoDecimals(roundUpNotificationDetails.getAmount()),
                        NOTIFICATION_DATA_KEY_CURRENCY,
                        roundUpNotificationDetails.getCurrency(),
                        NOTIFICATION_DATA_KEY_SAVINGS_POT_NAME,
                        roundUpNotificationDetails.getSavingsPotName(),
                        NOTIFICATION_DATA_KEY_TIMESTAMP,
                        formatTimestamp(LocalDateTime.now(), notificationTimezone)),
                String.valueOf(roundUpNotificationDetails.getUserId()),
                roundUpNotificationDetails.getTemplateName());
	}
	
	private BigDecimal getRoundUpAmount(ConfigurationEntity configurationEntity, String amount) {
		switch (configurationEntity.getLogicCode()) {
		case "TODO":
			return new BigDecimal("0.00"); // implement another round up logic
		case "DEFAULT":
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
			log.info("Not Eligible for round up due to not supported bank transaction code: {} or mcc ", bankTransactionCode);
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
		RequestSearchWalletDto requestSearchWalletDto = new RequestSearchWalletDto();
		requestSearchWalletDto.setAccountNumber(accountNumber);
		List<Wallet> walletList = walletClient.getUserWallet(requestSearchWalletDto, UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA, EventCode.EVENT_RECEIVE_TRANSACTION_DATA);
		return Optional.ofNullable(walletList).stream().flatMap(Collection::stream).findFirst()
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
	
	private String getSavingsPotName(ConfigurationEntity configurationEntity) {
		Optional<ConfigurationParamEntity> configurationParamEntity = configurationEntity.getConfigurationParamList()
				.stream().filter(param -> contributionWalletId.equals(param.getParamName())).findFirst();
		if (!configurationParamEntity.isPresent()) {
			throw new BadRequestException(ErrorCodes.ERR_SAVINGS_POT_NOT_FOUND_ERROR.getCode(),
					ErrorCodes.ERR_SAVINGS_POT_NOT_FOUND_ERROR.getMessage(), "");
		}
        RequestSearchWalletDto requestSearchWalletDto = new RequestSearchWalletDto();
        requestSearchWalletDto.setWalletId(configurationParamEntity.get().getValue());
        List<Wallet> walletList = walletClient.getUserWallet(requestSearchWalletDto, UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA, EventCode.EVENT_RECEIVE_TRANSACTION_DATA);

        return Optional.ofNullable(walletList).stream().flatMap(Collection::stream).findFirst()
				.map(Wallet::getWalletName).orElse("");
    }

}
