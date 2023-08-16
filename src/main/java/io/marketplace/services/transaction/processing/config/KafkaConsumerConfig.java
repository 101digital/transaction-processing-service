package io.marketplace.services.transaction.processing.config;

import io.marketplace.services.pxchange.client.config.KafkaExtraProperties;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.server}")
    private String bootstrapServers;

    @Value("${kafka.group-id:scheduled-payment-group}")
    private String consumerGroupId;

    @Value("${kafka.consumer.number-processor:2}")
    private int numberProcessor;

    private String KAFKA_TIMEOUT = "60000";
    private String KAFKA_COMMIT_INTERVAL = "5000";
    private String HEARTBEAT_INTERVAL_MS_CONFIG = "20000";

    @Bean
    public Map<String, Object> consumerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, KAFKA_TIMEOUT);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, KAFKA_COMMIT_INTERVAL);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, HEARTBEAT_INTERVAL_MS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory(
            KafkaProperties kafkaProperties, KafkaExtraProperties appProps) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> consumerConfigs = this.consumerConfigs(kafkaProperties);
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs);
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(numberProcessor);
        return factory;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(numberProcessor);
    }

}
