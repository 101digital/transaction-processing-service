package io.marketplace.services.transaction.processing.utils;

import com.google.gson.Gson;
import io.marketplace.commons.exception.ApiResponseException;
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

        String type = Optional.ofNullable(configurations).map(Configurations::getType).orElse("");

        if (StringUtils.isEmpty(type)) {
            throw ApiResponseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .message(ErrorCodes.MISSING_CONFIGURATION_TYPE.getMessage())
                    .code(ErrorCodes.MISSING_CONFIGURATION_TYPE.getCode())
                    .build();
        }

        if (!configurationTypes.contains(type)) {
            throw ApiResponseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .message(ErrorCodes.INVALID_CONFIGURATION_TYPE.getMessage())
                    .code(ErrorCodes.INVALID_CONFIGURATION_TYPE.getCode())
                    .build();
        }

        String logicCode =
                Optional.ofNullable(configurations).map(Configurations::getLogicCode).orElse("");

        if (StringUtils.isEmpty(logicCode)) {
            throw ApiResponseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .message(ErrorCodes.MISSING_CONFIGURATION_LOGIC_CODE.getMessage())
                    .code(ErrorCodes.MISSING_CONFIGURATION_LOGIC_CODE.getCode())
                    .build();
        }

        if (!configurationLogicCodes.contains(logicCode)) {
            throw ApiResponseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .message(ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getMessage())
                    .code(ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getCode())
                    .build();
        }

        Optional<ConfigurationEntity> configurationEntity =
                configurationRepository.findByTypeAndWallet(
                        configurations.getType(), configurations.getWalletId());

        if (configurationEntity.isPresent()) {
            throw ApiResponseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .message(ErrorCodes.ALREADY_CONFIGURATION_CREATED.getMessage())
                    .code(ErrorCodes.ALREADY_CONFIGURATION_CREATED.getCode())
                    .build();
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
}
