package com.kuro.expensetracker.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Object body;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(Object body){
        this.body = body;
    }
}
