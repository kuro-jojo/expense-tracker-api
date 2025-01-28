package com.kuro.expensetracker.services.transaction;

import com.kuro.expensetracker.requests.CategorizedTransactionRequest;
import com.kuro.expensetracker.requests.TransactionToCategorizeRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCategorizationService {
    @Value("${bert-cat-api.url}")
    private String apiUrl;

    public CategorizedTransactionRequest categorizeTransaction(@NotNull TransactionToCategorizeRequest transaction) {
        log.info("Categorizing transaction ({}) \"{}\"", transaction.type(), transaction.description());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionToCategorizeRequest> entity = new HttpEntity<>(transaction, headers);
        return new RestTemplate().postForEntity(apiUrl, entity, CategorizedTransactionRequest.class).getBody();
    }
}