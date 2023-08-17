package io.marketplace.services.transaction.processing.api;

import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationsApiDelegateImpl implements ConfigurationsApiDelegate {

    @Autowired private ConfigurationService configurationService;

    @Override
    public ResponseEntity<ConfigurationsResponse> addConfigurations(Configurations configurations) {

        return ResponseEntity.ok().body(configurationService.addConfigurations(configurations));
    }
}
