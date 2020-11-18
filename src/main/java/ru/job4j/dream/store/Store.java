package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    private static final Store INST = new Store();

    private Map<Integer, Post> posts = new ConcurrentHashMap<>();

    public Store() {
        posts.put(1, new Post(1, "Junior Java Job", "Java8", "18.11.20"));
        posts.put(2, new Post(2, "Middle Java Job", "Java8,AngularJs", "18.11.20"));
        posts.put(3, new Post(3, "Senior Java Job", "Java8,AngularJs,PostgresSQL", "18.11.20"));
    }

    public static Store instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
