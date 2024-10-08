package com.algon.loan.manager.controller;

import com.algon.loan.contract.response.CreateTrancheResponse;
import com.algon.loan.contract.response.DefaultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.algon.loan.contract.request.CreateTrancheRequest;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tranche")
public class TrancheController {

    private final KafkaTemplate<String, CreateTrancheRequest> trancheRequestKafkaTemplate;

    @PostMapping
    public DefaultResponse createTranche(@RequestBody CreateTrancheRequest request) throws ExecutionException, InterruptedException {
        return trancheRequestKafkaTemplate.send("trancheRequestTopic", request)
            .thenApplyAsync(value -> {
                log.info("Request '{}' successfully sent", request.getCorrelationId());
                return new DefaultResponse(
                        request.getCorrelationId(),
                        "Request successfully sent",
                        null);
            })
            .exceptionallyAsync(e -> {
                log.error("Request '{}' failed: {}", request.getCorrelationId(), e.getMessage(), e);
                return new DefaultResponse(
                        request.getCorrelationId(),
                        null,
                        "Request failed: " + e.getMessage());
            }).get();
    }
}
