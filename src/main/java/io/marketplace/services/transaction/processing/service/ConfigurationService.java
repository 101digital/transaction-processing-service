package io.marketplace.services.transaction.processing.service;

import com.google.gson.Gson;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.exception.NotFoundException;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.services.transaction.processing.common.ErrorCodes;
import io.marketplace.services.transaction.processing.dto.RequestSearchConfiguration;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import io.marketplace.services.transaction.processing.entity.ConfigurationParamEntity;
import io.marketplace.services.transaction.processing.model.Configurations;
import io.marketplace.services.transaction.processing.model.ConfigurationsListResponse;
import io.marketplace.services.transaction.processing.model.ConfigurationsResponse;
import io.marketplace.services.transaction.processing.model.PagingInformation;
import io.marketplace.services.transaction.processing.repository.ConfigurationRepository;
import io.marketplace.services.transaction.processing.specification.ConfigurationsSpecification;
import io.marketplace.services.transaction.processing.utils.ConfigurationUtils;
import io.marketplace.services.transaction.processing.utils.Constants.EventCode;
import io.marketplace.services.transaction.processing.utils.Constants.EventTitle;
import io.marketplace.services.transaction.processing.utils.Constants.UseCase;
import io.marketplace.services.transaction.processing.utils.EventTrackingService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired private ConfigurationRepository configurationRepository;

    @Autowired private ConfigurationUtils configurationUtils;

    @Autowired private EventTrackingService eventTrackingService;

    @Value("#{'${configuration.logic-codes}'.split(',')}")
    private List<String> configurationLogicCodes;

    @Autowired private Gson gson;

    public ConfigurationsResponse addConfigurations(Configurations configurations) {

        String businessId = String.format("Configuration: %s", configurations);

        eventTrackingService.traceEvent(
                UseCase.ADD_CONFIGURATION,
                EventCode.ADD_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_REQUEST,
                EventTitle.ADD_CONFIGURATION_REQUEST,
                businessId,
                configurations);

        configurationUtils.validateConfiguration(configurations);

        try {
            ConfigurationEntity configurationEntity =
                    configurationUtils.toConfigurationEntity(configurations);

            List<ConfigurationParamEntity> list =
                    configurationUtils.toConfigurationParamEntity(
                            configurations.getSupplementaryData(), configurationEntity.getId());

            configurationEntity.setConfigurationParamList(list);

            configurationEntity = configurationRepository.save(configurationEntity);

            eventTrackingService.traceEvent(
                    UseCase.ADD_CONFIGURATION,
                    EventCode.ADD_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.ADD_CONFIGURATION_REQUEST,
                    businessId,
                    configurations);

            return configurationUtils.toConfigurationResponse(configurationEntity);

        } catch (Exception exception) {
            log.error(ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(), exception);
            eventTrackingService.traceError(
                    UseCase.ADD_CONFIGURATION,
                    EventCode.ADD_CONFIGURATION_EVENT_CODE,
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(),
                    businessId,
                    exception);

            throw new InternalServerErrorException(
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_ADD_CONFIGURATIONS.getMessage(),
                    businessId);
        }
    }

    @Transactional
    public void deleteConfigurationById(UUID configurationId) {

        String businessId = String.format("Configuration Id: %s", configurationId);

        eventTrackingService.traceEvent(
                UseCase.DELETE_CONFIGURATION,
                EventCode.DELETE_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_REQUEST,
                EventTitle.DELETE_CONFIGURATION_REQUEST,
                businessId,
                configurationId);

        Optional<ConfigurationEntity> optConfig = configurationRepository.findById(configurationId);
        if (!optConfig.isPresent()) {
            throw new NotFoundException(
                    ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getCode(),
                    ErrorCodes.ERR_DELETE_CONFIGURATION_NOT_FOUND_ERROR.getMessage(),
                    configurationId.toString());
        }

        try {
            configurationRepository.deleteById(configurationId);

            eventTrackingService.traceEvent(
                    UseCase.DELETE_CONFIGURATION,
                    EventCode.DELETE_CONFIGURATION_EVENT_CODE + EventCode.SEQUENCE_RESPONSE,
                    EventTitle.DELETE_CONFIGURATION_RESPONSE,
                    businessId,
                    configurationId);

        } catch (Exception e) {
            log.error(ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(), e);

            eventTrackingService.traceError(
                    UseCase.DELETE_CONFIGURATION,
                    EventCode.DELETE_CONFIGURATION_EVENT_CODE,
                    ErrorCodes.ERR_DELETE_DB_ERROR.getCode(),
                    ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(),
                    businessId,
                    e);
            throw new InternalServerErrorException(
                    ErrorCodes.ERR_DELETE_DB_ERROR.getCode(),
                    ErrorCodes.ERR_DELETE_DB_ERROR.getMessage(),
                    configurationId.toString());
        }
    }

    public ConfigurationsListResponse getConfigurations(
            String walletId, String type, Integer pageNumber, Integer pageSize, String listOrders) {

        String businessId =
                String.format("Get Configurations by WalletId: %s and Type: %s", walletId, type);

        log.info(
                "Get Configurations by WalletId: {}, Type: {}, listOfOrders: {},pageNumber: {}, pageSize: {}",
                walletId,
                type,
                listOrders,
                pageNumber,
                pageSize);

        eventTrackingService.traceEvent(
                UseCase.GET_CONFIGURATIONS,
                EventCode.GET_CONFIGURATIONS + EventCode.SEQUENCE_REQUEST,
                UseCase.GET_CONFIGURATIONS,
                businessId,
                walletId);

        if (StringUtils.isEmpty(walletId)) {

            eventTrackingService.traceError(
                    UseCase.GET_CONFIGURATIONS,
                    EventCode.GET_CONFIGURATIONS,
                    ErrorCodes.MISSING_WALLET_ID.getCode(),
                    ErrorCodes.MISSING_WALLET_ID.getMessage(),
                    businessId,
                    null);

            throw new BadRequestException(
                    ErrorCodes.MISSING_WALLET_ID.getCode(),
                    ErrorCodes.MISSING_WALLET_ID.getMessage(),
                    businessId);
        }

        try {

            pageNumber = io.marketplace.commons.utils.StringUtils.normalizePageNumber(pageNumber);
            pageSize = io.marketplace.commons.utils.StringUtils.normalizePageSize(pageSize);

            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

            ConfigurationsSpecification configurationsSpecification =
                    new ConfigurationsSpecification(
                            RequestSearchConfiguration.builder()
                                    .listOrders(listOrders)
                                    .walletId(walletId)
                                    .type(type)
                                    .build());

            Page<ConfigurationEntity> page =
                    configurationRepository.findAll(configurationsSpecification, pageable);

            List<Configurations> configurations =
                    page.getContent().stream()
                            .map(
                                    configurationEntity ->
                                            configurationUtils.toConfiguration(configurationEntity))
                            .toList();

            eventTrackingService.traceEvent(
                    UseCase.GET_CONFIGURATIONS,
                    EventCode.GET_CONFIGURATIONS + EventCode.SEQUENCE_RESPONSE,
                    UseCase.GET_CONFIGURATIONS,
                    businessId,
                    walletId);

            return ConfigurationsListResponse.builder()
                    .data(configurations)
                    .paging(
                            PagingInformation.builder()
                                    .pageNumber(pageNumber)
                                    .pageSize(page.getSize())
                                    .totalRecords(page.getTotalElements())
                                    .build())
                    .build();

        } catch (Exception exception) {

            log.error("Exception message: {}", exception.getMessage());

            eventTrackingService.traceError(
                    UseCase.GET_CONFIGURATIONS,
                    EventCode.GET_CONFIGURATIONS,
                    ErrorCodes.ERROR_WHILE_GET_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_GET_CONFIGURATIONS.getMessage(),
                    businessId,
                    exception);

            throw new InternalServerErrorException(
                    ErrorCodes.ERROR_WHILE_GET_CONFIGURATIONS.getCode(),
                    ErrorCodes.ERROR_WHILE_GET_CONFIGURATIONS.getMessage(),
                    businessId);
        }
    }

    public ConfigurationsResponse updateConfigurationById(
            UUID configurationId, Configurations configurations) {

        String businessId =
                String.format(
                        "Update ConfigurationId: %s , Configuration: %s",
                        configurationId, configurations);

        log.info(
                "Update Configuration Request , ConfigurationId: {} , Configuration: {}",
                configurationId,
                configurations);

        eventTrackingService.traceEvent(
                UseCase.UPDATE_CONFIGURATIONS,
                EventCode.PUT_CONFIGURATIONS + EventCode.SEQUENCE_REQUEST,
                UseCase.UPDATE_CONFIGURATIONS,
                businessId,
                configurationId);

        Optional<ConfigurationEntity> optionalConfigurationEntity =
                configurationRepository.findById(configurationId);

        if (optionalConfigurationEntity.isEmpty()) {
            throw new BadRequestException(
                    ErrorCodes.INVALID_CONFIGURATION_ID.getCode(),
                    ErrorCodes.INVALID_CONFIGURATION_ID.getMessage(),
                    businessId);
        } else {

            ConfigurationEntity configurationEntity = optionalConfigurationEntity.get();

            if (!configurationEntity
                    .getType()
                    .equalsIgnoreCase(
                            Optional.ofNullable(configurations)
                                    .map(Configurations::getType)
                                    .orElse(""))) {
                throw new BadRequestException(
                        ErrorCodes.INVALID_CONFIGURATION_TYPE.getCode(),
                        ErrorCodes.INVALID_CONFIGURATION_TYPE.getMessage(),
                        businessId);
            }

            if (!configurationEntity
                    .getWallet()
                    .equalsIgnoreCase(
                            Optional.ofNullable(configurations)
                                    .map(Configurations::getWalletId)
                                    .orElse(""))) {
                throw new BadRequestException(
                        ErrorCodes.INVALID_WALLET_ID.getCode(),
                        ErrorCodes.INVALID_WALLET_ID.getMessage(),
                        businessId);
            }

            if (configurations != null && !StringUtils.isEmpty(configurations.getLogicCode())) {
                if (configurationLogicCodes.contains(configurations.getLogicCode())) {
                    configurationEntity.setLogicCode(configurations.getLogicCode());
                } else {
                    throw new BadRequestException(
                            ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getCode(),
                            ErrorCodes.INVALID_CONFIGURATION_LOGIC_CODE.getMessage(),
                            businessId);
                }
            }

            try {

                Map<String, String> supplementaryData =
                        gson.fromJson(
                                gson.toJson(configurations.getSupplementaryData()), Map.class);

                List<ConfigurationParamEntity> configurationParamEntityList =
                        configurationEntity.getConfigurationParamList();

                List<ConfigurationParamEntity> temp = new ArrayList<>();

                supplementaryData.entrySet().stream()
                        .forEach(
                                entry -> {
                                    configurationParamEntityList.stream()
                                            .forEach(
                                                    param -> {
                                                        if (param.getParamName()
                                                                .equalsIgnoreCase(entry.getKey())) {
                                                            param.setValue(entry.getValue());
                                                        } else {
                                                            temp.add(
                                                                    ConfigurationParamEntity
                                                                            .builder()
                                                                            .id(UUID.randomUUID())
                                                                            .configurationId(
                                                                                    configurationId)
                                                                            .paramName(
                                                                                    entry.getKey())
                                                                            .value(entry.getValue())
                                                                            .build());
                                                        }
                                                    });
                                });

                configurationParamEntityList.addAll(temp);

                configurationEntity.setConfigurationParamList(configurationParamEntityList);

                configurationEntity.setUpdatedAt(LocalDateTime.now());

                configurationEntity = configurationRepository.save(configurationEntity);

                eventTrackingService.traceEvent(
                        UseCase.UPDATE_CONFIGURATIONS,
                        EventCode.PUT_CONFIGURATIONS + EventCode.SEQUENCE_RESPONSE,
                        UseCase.UPDATE_CONFIGURATIONS,
                        businessId,
                        configurationId);

                return configurationUtils.toConfigurationResponse(configurationEntity);

            } catch (Exception exception) {

                log.error("Error While Updating Configurations: {}", exception);

                throw new InternalServerErrorException(
                        ErrorCodes.ERROR_UPDATE_CONFIGURATIONS.getCode(),
                        ErrorCodes.ERROR_UPDATE_CONFIGURATIONS.getMessage(),
                        businessId);
            }
        }
    }
}
