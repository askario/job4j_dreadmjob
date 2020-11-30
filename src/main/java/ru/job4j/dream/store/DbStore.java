package ru.job4j.dream.store;

import java.util.Collection;

public interface DbStore<T> {
    Collection<T> findAll();

    void save(T t);

    T findById(int id);

    void delete(T t);

    void update(T t);
}
