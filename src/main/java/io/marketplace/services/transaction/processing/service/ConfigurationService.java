package io.marketplace.services.transaction.processing.service;

import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.exception.NotFoundException;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
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
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService {
	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	
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
        	log.error(ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(), exception);
            eventTrackingService.traceError(
                    UseCase.ADD_CONFIGURATION,
                    EventCode.ADD_CONFIGURATION_EVENT_CODE,
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(),
                    businessId,
                    exception);

            throw new InternalServerErrorException(
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(),
                    businessId);
        }
    }
    
    @Transactional
    public void deleteConfigurationById(String configurationId) {
    	
    	String businessId = String.format("Configuration Id: %s", configurationId);
    	
    	eventTrackingService.traceEvent(
                UseCase.DELETE_CONFIGURATION,
                EventCode.DELETE_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_REQUEST,
                EventTitle.DELETE_CONFIGURATION_REQUEST,
                businessId,
                configurationId);
		
		Optional<ConfigurationEntity> optConfig = configurationRepository.findById(UUID.fromString(configurationId));
		if(!optConfig.isPresent()) {
			throw new NotFoundException(
            		ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getCode(), ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getMessage(), configurationId);
		}
		
		try {
			configurationRepository.deleteById(UUID.fromString(configurationId));
			
			eventTrackingService.traceEvent(
                    UseCase.DELETE_CONFIGURATION,
                    EventCode.DELETE_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.DELETE_CONFIGURATION_RESPONSE,
                    businessId,
                    configurationId);
		
		} catch (Exception e) {
        	log.error(ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(), e);
        	
        	eventTrackingService.traceError(
                    UseCase.DELETE_CONFIGURATION,
                    EventCode.DELETE_CONFIGURATION_EVENT_CODE,
                    ErrorCodes.ERR_DELETE_DB_ERROR.getCode(),
                    ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(),
                    businessId,
                    e);
            throw new InternalServerErrorException(
            		ErrorCodes.ERR_DELETE_DB_ERROR.getCode(), ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(), configurationId);
        }
		
	}
}
