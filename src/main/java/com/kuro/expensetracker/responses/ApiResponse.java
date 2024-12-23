package com.kuro.expensetracker.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private String message;
    private Integer total;
    private Object data;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(Object data) {
        this.data = data;
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, Object data, int total) {
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public ApiResponse(Object data, int total) {
        this.data = data;
        this.total = total;
    }
}
