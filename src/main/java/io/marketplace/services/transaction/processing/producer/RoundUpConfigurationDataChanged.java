package io.marketplace.services.transaction.processing.producer;

import com.google.gson.Gson;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.event.EventCategory;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.model.event.EventType;
import io.marketplace.services.pxchange.client.service.EventServiceClient;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.model.Configurations;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RoundUpConfigurationDataChanged {

    @Autowired private EventServiceClient eventServiceClient;

    private static final String ROUNDUP_CONTRIBUTION_TYPE = "ROUNDUP_CONTRIBUTION";

    @Value("${kafka.roundup-configuration-data-changed-topic:roundup-contribution-data-changed}")
    private String roundUpConfigurationDataChangedTopic;

    @Autowired private Gson gson;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RoundUpConfigurationDataChanged.class);

    @PostUpdate
    public void addUpdateEntityEvent(ConfigurationEntity configurationEntity) {

        if (configurationEntity.getType().equalsIgnoreCase(ROUNDUP_CONTRIBUTION_TYPE)) {

            Configurations configurations = toConfiguration(configurationEntity);
            EventMessage<Configurations> eventMessage =
                    eventServiceClient.buildEventMessage(configurations);
            eventMessage.setEventType(EventType.UPDATE);
            eventMessage.setEventCategory(EventCategory.CDC_EVENT);
            eventServiceClient.addEvent(roundUpConfigurationDataChangedTopic, eventMessage);
            LOGGER.info("RoundUpConfigurationDataChanged : {}", gson.toJson(configurations));
        }
    }

    @PostRemove
    public void addDeleteEntityEvent(ConfigurationEntity configurationEntity) {

        if (configurationEntity.getType().equalsIgnoreCase(ROUNDUP_CONTRIBUTION_TYPE)) {

            Configurations configurations = toConfiguration(configurationEntity);
            EventMessage<Configurations> eventMessage =
                eventServiceClient.buildEventMessage(configurations);
            eventMessage.setEventType(EventType.HARD_DELETE);
            eventMessage.setEventCategory(EventCategory.CDC_EVENT);
            eventServiceClient.addEvent(roundUpConfigurationDataChangedTopic, eventMessage);
            LOGGER.info("RoundUpConfigurationDataChanged : {}", gson.toJson(configurations));
        }
    }

    public Configurations toConfiguration(ConfigurationEntity configurationEntity) {

        List<ConfigurationParamEntity> configurationParamEntities =
            configurationEntity.getConfigurationParamList();

        Map<String, String> supplementaryData = new HashMap<>();

        configurationParamEntities.stream()
            .forEach(
                configurationParamEntity ->
                    supplementaryData.put(
                        configurationParamEntity.getParamName(),
                        configurationParamEntity.getValue()));

        return Configurations.builder()
            .id(configurationEntity.getId())
            .logicCode(configurationEntity.getLogicCode())
            .walletId(configurationEntity.getWallet())
            .type(configurationEntity.getType())
            .createdAt(configurationEntity.getCreatedAt())
            .updatedAt(configurationEntity.getUpdatedAt())
            .createdBy(configurationEntity.getCreatedBy())
            .updatedby(configurationEntity.getUpdatedBy())
            .supplementaryData(supplementaryData)
            .build();
    }


}
