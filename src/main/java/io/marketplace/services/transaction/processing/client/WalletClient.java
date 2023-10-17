package io.marketplace.services.transaction.processing.client;

import com.google.gson.Gson;
import io.marketplace.commons.constant.Constants;
import io.marketplace.commons.exception.ApiResponseException;
import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.jwt.JWTFactory;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.dto.ObjectResponseDto;
import io.marketplace.services.transaction.processing.client.dto.RequestSearchWalletDto;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.dto.Wallet;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferRequest;
import io.marketplace.services.transaction.processing.dto.WalletFundTransferResponse;
import io.marketplace.services.transaction.processing.dto.WalletListResponse;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;
import io.marketplace.services.transaction.processing.utils.EventTrackingService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WalletClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletClient.class);

	@Autowired
	private EventTrackingService eventTracker;

	@Autowired
	@Qualifier("internal-rest-template")
	private RestTemplate restInternal;

	@Autowired
	private Gson gson;

	@Value("${wallet-service-base-url:http://wallet-service:8080}")
	private String walletServiceBaseUrl;

	@Autowired
	private JWTFactory jwtFactory;

	private final String PATH_FUND_TRANSFERS = "/transfers";
	public static final String WALLETS = "/wallets";

	public WalletFundTransferResponse walletFundTransfer(WalletFundTransferRequest walletFundTransferRequest,
			String userId, String useCase, String eventCode) {

		String businessId = String.format("WalletTransferRequest %s", walletFundTransferRequest.toString());

		eventTracker.traceEvent(useCase, eventCode + EventCode.SEQUENCE_INVOKE,
				"Received request for invoke Wallet Fund Transfer", businessId, walletFundTransferRequest);

		final String url = UriComponentsBuilder.fromUriString(walletServiceBaseUrl).path(PATH_FUND_TRANSFERS)
				.toUriString();

		HttpEntity<?> rqEntity = new HttpEntity<>(walletFundTransferRequest, getHttpHeadersForInternalCall(userId));

		var response = restInternal.exchange(url, HttpMethod.POST, rqEntity, ObjectResponseDto.class);

		Object object = Optional.ofNullable(response).map(ResponseEntity::getBody).map(ObjectResponseDto::getData)
				.orElseThrow(
						() -> ApiResponseException.builder().message(ErrorCodes.ERROR_GETTING_RESPONSE.getMessage())
								.code(ErrorCodes.ERROR_GETTING_RESPONSE.getCode())
								.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());

		String json = gson.toJson(object);

		WalletFundTransferResponse walletFundTransferResponse = gson.fromJson(json, WalletFundTransferResponse.class);

		return walletFundTransferResponse;
	}

	public List<Wallet> getUserWallet(RequestSearchWalletDto requestSearchWalletDto, String useCase, String eventCode) {

		String businessId = String.format("requestSearchWalletDto %s", requestSearchWalletDto.toString());

		LOGGER.info("Getting wallet info for given request search :" + requestSearchWalletDto);
		eventTracker.traceEvent(useCase, eventCode + EventCode.SEQUENCE_INVOKE,
				"Request received to invoke get wallet details", businessId, requestSearchWalletDto);
		try {
			final String url = UriComponentsBuilder.fromUriString(walletServiceBaseUrl).path(WALLETS)
					.queryParams(requestSearchWalletDto.getQueryParams()).toUriString();
			var response = restInternal.exchange(url, HttpMethod.GET, new HttpEntity<>(""), WalletListResponse.class);
			LOGGER.info("Getting wallet info for given request search response" + gson.toJson(response));
			WalletListResponse body = response.getBody();
			if (body != null) {
				return body.getData();
			} else {
				return Collections.emptyList();
			}

		} catch (Exception e) {
			LOGGER.error(ErrorCodes.ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER.getMessage(), e);
			eventTracker.traceError(useCase, eventCode, ErrorCodes.ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER.getCode(),
					ErrorCodes.ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER.getMessage(), businessId, e);
			throw new InternalServerErrorException(ErrorCodes.ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER.getCode(),
					ErrorCodes.ERROR_CALL_WALLET_SERVICE_ACCOUNT_NUMBER.getMessage(), businessId, e);
		}
	}

	private HttpHeaders getHttpHeadersForInternalCall(String userId) {
		final HttpHeaders headers = new HttpHeaders();
		String internalServiceJWT = jwtFactory.generateUserToken(userId);
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(Constants.X_JWT_ASSERTION_HEADER_NAME, internalServiceJWT);
		return headers;
	}
}
