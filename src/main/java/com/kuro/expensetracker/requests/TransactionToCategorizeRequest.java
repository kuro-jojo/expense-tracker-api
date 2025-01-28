package com.kuro.expensetracker.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionToCategorizeRequest(String id, String description, @JsonProperty("t_type") String type) {
    public TransactionToCategorizeRequest(String description, String type) {
        this(null, description, type);
    }
}