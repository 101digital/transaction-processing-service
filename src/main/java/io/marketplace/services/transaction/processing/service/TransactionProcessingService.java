package io.marketplace.services.transaction.processing.service;

import io.marketplace.commons.exception.ApiResponseException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.repository.ConfigurationParamRepository;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;
import io.marketplace.services.transaction.processing.utils.ConfigurationUtils;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.EventTitle;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;
import io.marketplace.services.transaction.processing.utils.EventTrackingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionProcessingService {

    @Autowired private ConfigurationRepository configurationRepository;

    @Autowired private ConfigurationParamRepository configurationParamRepository;

    @Autowired private ConfigurationUtils configurationUtils;

    @Autowired private EventTrackingService eventTrackingService;

    public ConfigurationsResponse addConfigurations(Configurations configurations) {

        String businessId = String.format("Configuration: %s", configurations);

        eventTrackingService.traceEvent(
                UseCase.ADD_CONFIGURATION,
                EventCode.ADD_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_REQUEST,
                EventTitle.ADD_CONFIGURATION_REQUEST,
                businessId,
                configurations);

        configurationUtils.validateConfiguration(configurations);

        try {
            ConfigurationEntity configurationEntity =
                    configurationUtils.toConfigurationEntity(configurations);

            configurationEntity = configurationRepository.save(configurationEntity);

            List<ConfigurationParamEntity> list =
                    configurationUtils.toConfigurationParamEntity(
                            configurations.getSupplementaryData(), configurationEntity.getId());

            configurationParamRepository.saveAll(list);

            eventTrackingService.traceEvent(
                    UseCase.ADD_CONFIGURATION,
                    EventCode.ADD_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.ADD_CONFIGURATION_REQUEST,
                    businessId,
                    configurations);

            return configurationUtils.toConfigurationResponse(configurationEntity);

        } catch (Exception exception) {
            eventTrackingService.traceError(
                    UseCase.ADD_CONFIGURATION,
                    EventCode.ADD_CONFIGURATION_EVENT_CODE,
                    "",
                    "",
                    businessId,
                    exception);

            throw ApiResponseException.builder()
                    .code(ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getCode())
                    .message(ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .throwable(exception)
                    .build();
        }
    }
}
