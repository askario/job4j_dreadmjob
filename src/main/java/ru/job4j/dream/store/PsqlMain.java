package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();

        Collection<Post> posts = store.findAllPosts();
        for (Post post : posts) {
            System.out.println(post);
        }

    }
}
