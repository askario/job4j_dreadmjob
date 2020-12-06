package ru.job4j.dream.servlet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GreetingRequest {
    private String email;

    public GreetingRequest(String email) {
        this.email = email;
    }

    public GreetingRequest() {

    }
}
