package io.marketplace.services.transaction.processing.config;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.services.pxchange.client.common.DataConstants;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

/**
 * Kafka Producer configuration class.
 */
@Configuration
@ComponentScan
public class KafkaProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerConfig.class);

    @Value("${kafka.server}")
    private String bootstrapServers;

    @Value("${kafka.consumer.scheduled-payment-start:scheduled-payment-start}")
    private String scheduledPaymentStartTopic;

    @Value("${kafka.consumer.number-processor:2}")
    private int numberProcessor;

    @Autowired(required = false)
    private KafkaProperties kafkaProperties;


    @Bean(name = "ScheduledPaymentProducerKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        LOGGER.info("Creating kafkaTemplate with Producer bootstrapServers: {}", bootstrapServers);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createSchedulePaymentStartDataTopic() {
        return new NewTopic(scheduledPaymentStartTopic, numberProcessor, (short) 1);
    }
}
