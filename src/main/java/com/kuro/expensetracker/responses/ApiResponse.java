package com.kuro.expensetracker.responses;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private Boolean success;
    private Integer status;
    private String message;
    private Integer total;
    private Map<String, Object> additionalContent;

    public ApiResponse(boolean success, Integer status) {
        this.success = success;
        this.status = status;
        additionalContent = new HashMap<>();
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalContent() {
        return additionalContent;
    }

    public void addContent(String key, Object value) {
        additionalContent.put(key, value);
    }
}
