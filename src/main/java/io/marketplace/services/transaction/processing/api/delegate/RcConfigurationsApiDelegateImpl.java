package io.marketplace.services.transaction.processing.api.delegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.marketplace.services.transaction.processing.api.RcConfigurationsApiDelegate;
import io.marketplace.services.transaction.processing.service.RcConfigurationsService;

@Service
public class RcConfigurationsApiDelegateImpl implements RcConfigurationsApiDelegate{
	
	@Autowired private RcConfigurationsService rcConfigurationsService;

	@Override
	public ResponseEntity<String> deleteConfigurationById(String configurationId) {
		rcConfigurationsService.deleteConfigurationById(configurationId);
		return ResponseEntity.noContent().build();
	}

	
}
