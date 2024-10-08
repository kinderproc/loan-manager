package com.algon.loan.manager.listener;

import com.algon.loan.contract.response.CreateTrancheResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTrancheResponseListener implements MessageListener<String, CreateTrancheResponse> {

    @Override
    public void onMessage(ConsumerRecord<String, CreateTrancheResponse> consumerRecord) {
        log.info("Received message from {}: {}", "trancheResponseTopic", consumerRecord.value().getMessage());
    }
}
