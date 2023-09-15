package io.marketplace.services.transaction.processing;

import io.marketplace.commons.application.BaseSpringBootApplication;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.validator.BankInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@ComponentScan(basePackages = {"io.marketplace.services", "io.marketplace.commons"}, excludeFilters={
        @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value= BankInformationCache.class)})
@EnableTransactionManagement
public class TransactionProcessingApplication extends BaseSpringBootApplication {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcessingApplication.class);
    @Autowired private BuildProperties buildProperties;
    @Value("${app-config.server.timezone:UTC}")
    private String timezone;

    public static void main(String[] args) {
        SpringApplication.run(TransactionProcessingApplication.class, args);
    }

    @PostConstruct
    private void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        log.info(
                "Application version {}, build time: {}, gitCommit: {}, jdk: {}, timezone: {}",
                buildProperties.getVersion(),
                LocalDateTime.ofInstant(buildProperties.getTime(), ZoneOffset.UTC),
                buildProperties.get("gitCommitId"),
                System.getProperty("java.version"),
                timezone
          );
    }
}
