package io.marketplace.services.transaction.processing.api;

import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsListResponse;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationsApiDelegateImpl implements ConfigurationsApiDelegate {

    @Autowired private ConfigurationService configurationService;

    @Override
    public ResponseEntity<ConfigurationsResponse> addConfigurations(Configurations configurations) {

        return ResponseEntity.ok().body(configurationService.addConfigurations(configurations));
    }

    @Override
    public ResponseEntity<String> deleteConfigurationById(String configurationId) {
        configurationService.deleteConfigurationById(configurationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ConfigurationsListResponse> getConfigurations(
            String walletId, String type, Integer pageNumber, Integer pageSize, String listOrders) {
        return ResponseEntity.ok()
                .body(
                        configurationService.getConfigurations(
                                walletId, type, pageNumber, pageSize, listOrders));
    }
}
