package io.marketplace.services.transaction.processing.service;

import java.util.Optional;
import java.util.UUID;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.exception.NotFoundException;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;

@Service
public class RcConfigurationsService {
	private static final Logger log = LoggerFactory.getLogger(RcConfigurationsService.class);
	
	@Autowired private ConfigurationRepository configurationRepository;
	
	public void deleteConfigurationById(String configurationId) {
		try {
			Optional<ConfigurationEntity> optConfig = configurationRepository.findById(UUID.fromString(configurationId));
			if(!optConfig.isPresent()) {
				throw new NotFoundException(
	            		ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getCode(), ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getMessage(), configurationId);
			}
			
			configurationRepository.deleteById(UUID.fromString(configurationId));
		
		} catch (Exception e) {
        	log.error(ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(), e);
            throw new InternalServerErrorException(
            		ErrorCodes.ERR_DELETE_DB_ERROR.getCode(), ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(), configurationId);
        }
		
	}

}
