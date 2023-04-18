package com.example.webSpring.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
