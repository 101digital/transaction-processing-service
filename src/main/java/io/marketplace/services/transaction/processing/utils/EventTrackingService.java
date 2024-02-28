package io.marketplace.services.transaction.processing.utils;

import com.google.gson.Gson;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.CustomRequestContext;
import io.marketplace.commons.model.event.EventCategory;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.model.event.EventStatus;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.commons.utils.StringUtils;
import io.marketplace.commons.utils.ThreadContextUtils;
import io.marketplace.services.pxchange.client.service.PXChangeServiceClient;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventTrackingService {


    private static final Logger LOGGER = LoggerFactory.getLogger(EventTrackingService.class);
    public static final String SYSTEM = "SYSTEM";

    @Value("${spring.application.id:}")
    private String serviceId;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.entity-id}")
    private String entityId;

    @Autowired private PXChangeServiceClient pxClient;
    @Autowired private Gson gson;

    public <T> void traceEvent(
            String activityName,
            String eventCode,
            String eventTitle,
            String businessId,
            T businessData) {
        final EventMessage<T> eventMessage = EventMessage.<T>builder()
          .appId(SYSTEM)
          .eventCategory(EventCategory.EVENT)
          .eventUser(MembershipUtils.getUserId())
          .eventTitle(eventTitle)
          .eventBusinessId(businessId)
          .eventCode(eventCode)
          .activityName(activityName)
          .entityId(SYSTEM)
          .eventSource(applicationName)
          .eventStatus(EventStatus.SUCCESS)
          .businessData(businessData)
          .build();
        LOGGER.info("traceEvent {}", gson.toJson(eventMessage));
        pxClient.addEvent(eventMessage);
    }

    public void traceError(
            String activityName,
            String eventCode,
            String errorCode,
            String errorMessage,
            String businessId,
            Exception ex) {
        traceError(
                activityName,
                eventCode,
                errorCode,
                errorMessage,
                businessId,
                ex != null ? ex.getMessage() : null);
    }

    public <T> void traceError(
            String activityName,
            String eventCode,
            String errorCode,
            String eventTitle,
            String businessId,
            T businessData) {
        final EventMessage<T> eventMessage = EventMessage.<T>builder()
          .errorCode(errorCode)
          .appId(SYSTEM)
          .eventCategory(EventCategory.ERROR)
          .eventUser(MembershipUtils.getUserId())
          .eventTitle(eventTitle)
          .eventBusinessId(businessId)
          .errorCode(errorCode)
          .activityName(activityName)
          .businessData(businessData)
          .eventCode(eventCode)
          .entityId(SYSTEM)
          .eventSource(applicationName)
          .eventStatus(EventStatus.FAILED)
          .build();
        LOGGER.info("traceError {}", gson.toJson(eventMessage));
        pxClient.addEvent(eventMessage);
    }

    public void initProcessorContext(String requestId) {
        if (StringUtils.isEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        CustomRequestContext customRequestContext =
                CustomRequestContext.builder()
                        .requestId(requestId)
                        .serviceId(serviceId)
                        .serviceName(applicationName)
                        .build();

        ThreadContextUtils.setCustomRequest(customRequestContext);
    }
}
