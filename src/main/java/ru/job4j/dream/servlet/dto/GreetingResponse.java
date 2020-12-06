package ru.job4j.dream.servlet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class GreetingResponse {
    private String text;

    public GreetingResponse() {
    }

    public GreetingResponse(String text) {
        this.text = text;
    }
}
