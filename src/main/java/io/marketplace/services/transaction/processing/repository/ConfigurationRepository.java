package io.marketplace.services.transaction.processing.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;

public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, UUID> {

}
