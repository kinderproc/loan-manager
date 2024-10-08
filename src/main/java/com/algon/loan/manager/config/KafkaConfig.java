package com.algon.loan.manager.config;

import com.algon.loan.contract.request.CreateTrancheRequest;
import com.algon.loan.contract.response.CreateTrancheResponse;
import com.algon.loan.manager.listener.CreateTrancheResponseListener;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public Map<String, Object> trancheRequestProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    @Bean
    public ProducerFactory<String, CreateTrancheRequest> trancheRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(trancheRequestProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, CreateTrancheRequest> trancheRequestKafkaTemplate() {
        return new KafkaTemplate<>(trancheRequestProducerFactory());
    }

    @Bean
    public KafkaMessageListenerContainer<String, CreateTrancheResponse> trancheRequestListenerContainer(CreateTrancheResponseListener listener) {
        return new KafkaMessageListenerContainer<>(trancheResponseConsumerFactory(), trancheResponseContainerProperties(listener));
    }

    @Bean
    public ConsumerFactory<String, CreateTrancheResponse> trancheResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(new TypeReference<>(){}));
    }

    @Bean
    public ContainerProperties trancheResponseContainerProperties(
            CreateTrancheResponseListener listener
    ) {
        String[] topics = {"trancheResponseTopic"};
        ContainerProperties containerProperties = new ContainerProperties(topics);
        containerProperties.setMessageListener(listener);
        containerProperties.setPollTimeout(1000);
        containerProperties.setGroupId("trancheResponseGroup");
        return containerProperties;
    }

}
