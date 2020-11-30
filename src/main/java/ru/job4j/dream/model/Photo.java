package ru.job4j.dream.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Photo {
    private int id;
    private String name;


    public Photo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Photo(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id == photo.id &&
                Objects.equals(name, photo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
