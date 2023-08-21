package io.marketplace.services.transaction.processing.utils;

import com.google.gson.Gson;
import io.marketplace.commons.exception.ApiResponseException;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.repository.ConfigurationParamRepository;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationUtils {

    @Value("#{'${configuration.types}'.split(',')}")
    private List<String> configurationTypes;

    @Value("#{'${configuration.logic-codes}'.split(',')}")
    private List<String> configurationLogicCodes;

    @Autowired private ConfigurationRepository configurationRepository;

    @Autowired private ConfigurationParamRepository configurationParamRepository;

    @Autowired private Gson gson;

    public void validateConfiguration(Configurations configurations) {

        String businessId = String.format("Configuration: %s", configurations);

        String type = Optional.ofNullable(configurations).map(Configurations::getType).orElse("");

        if (StringUtils.isEmpty(type)) {

            throw new BadRequestException(
                    ErrorCodes.MISSING_CONFIGURATION_TYPE.getCode(),
                    ErrorCodes.MISSING_CONFIGURATION_TYPE.getMessage(),
                    businessId);
        }

        if (!configurationTypes.contains(type)) {

            throw new BadRequestException(
                    ErrorCodes.INVALID_CONFIGURATION_TYPE.getCode(),
                    ErrorCodes.INVALID_CONFIGURATION_TYPE.getMessage(),
                    businessId);
        }

        String logicCode =
                Optional.ofNullable(configurations).map(Configurations::getLogicCode).orElse("");

        if (StringUtils.isEmpty(logicCode)) {

            throw new BadRequestException(
                    ErrorCodes.MISSING_CONFIGURATION_LOGIC_CODE.getCode(),
                    ErrorCodes.MISSING_CONFIGURATION_LOGIC_CODE.getMessage(),
                    businessId);
        }

        if (!configurationLogicCodes.contains(logicCode)) {

            throw new BadRequestException(
                    ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getCode(),
                    ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getMessage(),
                    businessId);
        }

        Optional<ConfigurationEntity> configurationEntity =
                configurationRepository.findByTypeAndWallet(
                        Optional.ofNullable(configurations).map(Configurations::getType).orElse(""),
                        Optional.ofNullable(configurations)
                                .map(Configurations::getWalletId)
                                .orElse(""));

        if (configurationEntity.isPresent()) {

            throw new BadRequestException(
                    ErrorCodes.ALREADY_CONFIGURATION_CREATED.getCode(),
                    ErrorCodes.ALREADY_CONFIGURATION_CREATED.getMessage(),
                    businessId);
        }
    }

    public ConfigurationEntity toConfigurationEntity(Configurations configurations) {

        return ConfigurationEntity.builder()
                .id(UUID.randomUUID())
                .type(configurations.getType())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(UUID.fromString(MembershipUtils.getUserId()))
                .updatedBy(UUID.fromString(MembershipUtils.getUserId()))
                .wallet(configurations.getWalletId())
                .sendNotification(Boolean.FALSE)
                .build();
    }

    public List<ConfigurationParamEntity> toConfigurationParamEntity(
            Object supplementaryData, UUID configurationId) {

        Map<String, String> data = gson.fromJson(gson.toJson(supplementaryData), Map.class);

        List<ConfigurationParamEntity> list = new ArrayList<>();

        data.entrySet().stream()
                .forEach(
                        entry ->
                                list.add(
                                        ConfigurationParamEntity.builder()
                                                .id(UUID.randomUUID())
                                                .configurationId(configurationId)
                                                .paramName(entry.getKey())
                                                .value(entry.getValue())
                                                .build()));

        return list;
    }

    public ConfigurationsResponse toConfigurationResponse(ConfigurationEntity configurationEntity) {

        List<ConfigurationParamEntity> list =
                configurationParamRepository.findByConfigurationId(configurationEntity.getId());

        Map<String, String> supplementaryData = new HashMap<>();

        list.stream()
                .forEach(
                        element ->
                                supplementaryData.put(element.getParamName(), element.getValue()));

        return ConfigurationsResponse.builder()
                .data(
                        Configurations.builder()
                                .id(configurationEntity.getId())
                                .type(configurationEntity.getType())
                                .logicCode(configurationEntity.getLogicCode())
                                .walletId(configurationEntity.getWallet())
                                .createdAt(configurationEntity.getCreatedAt())
                                .createdBy(configurationEntity.getCreatedBy())
                                .updatedAt(configurationEntity.getUpdatedAt())
                                .updatedby(configurationEntity.getUpdatedBy())
                                .supplementaryData(supplementaryData)
                                .build())
                .build();
    }

    public Configurations toConfiguration(ConfigurationEntity configurationEntity) {

        List<ConfigurationParamEntity> configurationParamEntities =
                configurationEntity.getConfigurationParamList();

        Map<String, String> supplementaryData = new HashMap<>();

        configurationParamEntities.stream()
                .forEach(
                        configurationParamEntity ->
                                supplementaryData.put(
                                        configurationParamEntity.getParamName(),
                                        configurationParamEntity.getValue()));

        return Configurations.builder()
                .id(configurationEntity.getId())
                .logicCode(configurationEntity.getLogicCode())
                .walletId(configurationEntity.getWallet())
                .type(configurationEntity.getType())
                .createdAt(configurationEntity.getCreatedAt())
                .updatedAt(configurationEntity.getUpdatedAt())
                .createdBy(configurationEntity.getCreatedBy())
                .updatedby(configurationEntity.getUpdatedBy())
                .supplementaryData(supplementaryData)
                .build();
    }
}
