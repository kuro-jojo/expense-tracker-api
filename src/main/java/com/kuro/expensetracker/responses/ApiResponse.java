package com.kuro.expensetracker.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private String message;
    private Integer size;
    private Object body;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(Object body) {
        this.body = body;
    }

    public ApiResponse(String message, Object body) {
        this.message = message;
        this.body = body;
    }

    public ApiResponse(String message, Object body, int size) {
        this.message = message;
        this.body = body;
        this.size = size;
    }
}
