package io.marketplace.services.transaction.processing.consumer;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import com.google.gson.Gson;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.CustomRequestContext;
import io.marketplace.commons.model.event.AsyncRequestScopeAttributes;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.utils.EventMessageUtils;
import io.marketplace.commons.utils.ThreadContextUtils;
import io.marketplace.services.transaction.processing.dto.openbanking.OBTransaction6;
import io.marketplace.services.transaction.processing.service.RoundUpContributionService;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.EventTitle;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;
import io.marketplace.services.transaction.processing.utils.EventTrackingService;

@Service
public class TransactionDataChangedConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionDataChangedConsumer.class);
	
	@Autowired private Gson gson;
	
	@Value("${spring.application.name:}")
    private String applicationName;

    @Value("${spring.application.id}")
    private String serviceId;
    
    @Autowired private EventMessageUtils eventMessageUtil;
    
    @Autowired private EventTrackingService eventTrackingService;
    
    @Autowired private RoundUpContributionService roundUpContributionService;
    
    @KafkaListener(
            topics = "${kafka.topics.transaction-data-changed:transaction-data-changed}",
            groupId = "${kafka.group-id:transaction-processing-service}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenWalletDataChangedEvent(@Payload String message) {
    	try {
    		RequestContextHolder.setRequestAttributes(new AsyncRequestScopeAttributes());
            MDC.put("eventTraceId", ThreadContextUtils.getCustomRequest().getRequestId());
            log.info("received transaction-data-changed data: {}", message);
            
            EventMessage<OBTransaction6> eventMessage =
                    eventMessageUtil.fromQueueMessage(message, OBTransaction6.class);
            String businessId = String.format("transactionId: %s", Optional.of(eventMessage)
            		.map(EventMessage::getBusinessData)
            		.map(OBTransaction6::getTransactionId)
            		.orElse(""));
            
            setupRequestContext();
            
            eventTrackingService.traceEvent(
                    UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA,
                    EventCode.EVENT_RECEIVE_TRANSACTION_DATA + EventCode.SEQUENCE_REQUEST,
                    EventTitle.TRANSACTION_DATA_RECV_REQUEST,
                    businessId,
                    eventMessage.getBusinessData());
            
            roundUpContributionService.processTransaction(eventMessage.getBusinessData());
            
            eventTrackingService.traceEvent(
                    UseCase.ACTIVITY_RECEIVE_TRANSACTION_DATA,
                    EventCode.EVENT_RECEIVE_TRANSACTION_DATA + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.TRANSACTION_DATA_RECV_RESPONSE,
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

}
