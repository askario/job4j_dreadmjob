package ru.job4j.dream.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {
    private int id;
    private String name;

    public City(String name) {
        this.name = name;
    }
}
