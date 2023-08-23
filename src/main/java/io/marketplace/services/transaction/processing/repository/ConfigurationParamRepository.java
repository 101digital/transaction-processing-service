package io.marketplace.services.transaction.processing.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;

public interface ConfigurationParamRepository extends JpaRepository<ConfigurationParamEntity, UUID> {

  List<ConfigurationParamEntity> findByConfigurationId(UUID configurationId);
  List<ConfigurationParamEntity> findByParamNameAndValue(String paramName, String value);

}
