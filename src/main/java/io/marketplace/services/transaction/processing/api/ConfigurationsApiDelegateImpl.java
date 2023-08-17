package io.marketplace.services.transaction.processing.api;

import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.service.TransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.marketplace.services.transaction.processing.api.ConfigurationsApiDelegate;

@Service
public class ConfigurationsApiDelegateImpl implements ConfigurationsApiDelegate {

    @Autowired private TransactionProcessingService transactionProcessingService;

    @Override
    public ResponseEntity<ConfigurationsResponse> addConfigurations(Configurations configurations) {

        return ResponseEntity.ok()
                .body(transactionProcessingService.addConfigurations(configurations));
    }
}
