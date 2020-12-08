package ru.job4j.dream.store;

import ru.job4j.dream.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StoreStub implements Store {
    private final List<Post> posts = new ArrayList<>();

    @Override
    public Collection<Post> findAllPosts() {
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return null;
    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public void save(Post post) {
        this.posts.add(post);
    }

    @Override
    public void save(Candidate candidate) {

    }

    @Override
    public void save(User user) {

    }

    @Override
    public Post findPostById(int id) {
        return null;
    }

    @Override
    public Candidate findCandidateById(int id) {
        return null;
    }

    @Override
    public User findUserById(int id) {
        return null;
    }

    @Override
    public Photo findPhotoById(int id) {
        return null;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Collection<City> getAllCities() {
        return null;
    }

    @Override
    public Optional<City> findCityById(int id) {
        return Optional.empty();
    }
}
