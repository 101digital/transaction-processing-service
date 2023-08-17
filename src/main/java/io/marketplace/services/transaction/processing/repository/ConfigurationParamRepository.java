package io.marketplace.services.transaction.processing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;

public interface ConfigurationParamRepository extends JpaRepository<ConfigurationParamEntity, UUID> {

}
