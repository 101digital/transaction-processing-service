package io.marketplace.services.transaction.processing.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository
        extends JpaRepository<ConfigurationEntity, UUID>,
                JpaSpecificationExecutor<ConfigurationEntity> {

    Optional<ConfigurationEntity> findById(UUID id);

    Optional<ConfigurationEntity> findByTypeAndWallet(String type, String wallet);
}
