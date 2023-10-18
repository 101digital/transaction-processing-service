package io.marketplace.services.transaction.processing.consumer;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.CustomRequestContext;
import io.marketplace.commons.model.event.AsyncRequestScopeAttributes;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.model.event.EventType;
import io.marketplace.commons.utils.EventMessageUtils;
import io.marketplace.commons.utils.ThreadContextUtils;
import io.marketplace.services.transaction.processing.dto.Wallet;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.repository.ConfigurationParamRepository;
import io.marketplace.services.transaction.processing.service.ConfigurationService;
import io.marketplace.services.transaction.processing.utils.EventTrackingService;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.EventTitle;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletDataChangedConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletDataChangedConsumer.class);

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${spring.application.id}")
    private String serviceId;

    @Value("${transaction-processing-config.savings-pot-product-id:SavingsPot-i}")
    private String savingPotProductId;

    @Value("${transaction-processing-config.contribution-param-name:contributionWalletId}")
    private String contributionWalletId;

    @Autowired private EventMessageUtils eventMessageUtil;

    @Autowired private ConfigurationService configurationService;

    @Autowired private ConfigurationParamRepository configurationParamRepository;

    @Autowired private EventTrackingService eventTrackingService;

    @KafkaListener(
            topics = "${kafka.topics.wallet-data-changed:wallet-data-changed}",
            groupId = "${kafka.group-id:transaction-processing-service}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenWalletDataChangedEvent(@Payload String message) {
        try {
            RequestContextHolder.setRequestAttributes(new AsyncRequestScopeAttributes());
            MDC.put("eventTraceId", ThreadContextUtils.getCustomRequest().getRequestId());
            log.info("received wallet-data-changed data: {}", message);

            EventMessage<Wallet> eventMessage =
                    eventMessageUtil.fromQueueMessage(message, Wallet.class);
            setupRequestContext();
            String businessId =
                    String.format(
                            "walletId: %s",
                            Optional.of(eventMessage)
                                    .map(EventMessage::getBusinessData)
                                    .map(Wallet::getWalletId)
                                    .orElse(""));

            eventTrackingService.traceEvent(
                    UseCase.RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA,
                    EventCode.RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA + EventCode.SEQUENCE_REQUEST,
                    EventTitle.WALLET_DATA_RECV_REQUEST,
                    businessId,
                    eventMessage.getBusinessData());

            if (!validateEvent(eventMessage)) {
                return;
            }

            Wallet wallet = eventMessage.getBusinessData();
            if (!validateWallet(wallet)) {
                return;
            }

            processTransactionProcessing(wallet);

            eventTrackingService.traceEvent(
                    UseCase.RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA,
                    EventCode.RECEIVE_WALLET_DATA_CHANGED_FROM_KAFKA + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.WALLET_DATA_RECV_RESPONSE,
                    businessId,
                    eventMessage.getBusinessData());

        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    private void setupRequestContext() {
        String requestId = UUID.randomUUID().toString();
        MDC.put("eventTraceId", requestId);
        CustomRequestContext customRequestContext =
                CustomRequestContext.builder().requestId(requestId).serviceId(serviceId).build();
        ThreadContextUtils.setCustomRequest(customRequestContext);
    }

    private boolean validateEvent(EventMessage<Wallet> eventMessage) {
        if (eventMessage == null || eventMessage.getEventType() != EventType.UPDATE) {
            log.info("received event is not update event: {}", eventMessage.getEventType());
            return false;
        }

        if (!EventCode.WALLET_CLOSED.equalsIgnoreCase(eventMessage.getEventCode())) {
            log.info("Received event is not a wallet closed event");
            return false;
        }

        return true;
    }

    private boolean validateWallet(Wallet wallet) {
        if (wallet == null || wallet.getBankAccount() == null) {
            log.info("Business data or bank account is empty");
            return false;
        }

        if (!savingPotProductId.equalsIgnoreCase(wallet.getBankAccount().getProductId())) {
            log.info("Received wallet does not belong to savings pot");
            return false;
        }

        return true;
    }

    private void processTransactionProcessing(Wallet wallet) {
        List<ConfigurationParamEntity> configurationParamList =
                configurationParamRepository.findByParamNameAndValue(
                        contributionWalletId, wallet.getWalletId());

        configurationParamList.forEach(
                configurationParamEntity -> {
                    configurationService.deleteConfigurationById(
                            configurationParamEntity.getConfigurationId());
                });
    }
}
