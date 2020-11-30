package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Photo;
import ru.job4j.dream.model.Post;

import java.util.Collection;

@Log4j2
public class PsqlStore implements Store {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();
    private final DbStore postData = PostData.instOf();
    private final DbStore candidateData = CandidateData.instOf();
    private final DbStore photoData = PhotoData.instOf();

    private PsqlStore() {
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }


    @Override
    public Collection<Post> findAllPosts() {
        return postData.findAll();
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return candidateData.findAll();
    }

    @Override
    public void save(Post post) {
        postData.save(post);
    }

    @Override
    public void save(Candidate candidate) {
        candidateData.save(candidate);
    }


    @Override
    public Post findPostById(int id) {
        return (Post) postData.findById(id);
    }

    @Override
    public Candidate findCandidateById(int id) {
        return (Candidate) candidateData.findById(id);
    }

    public Photo findPhotoById(int id) {
        return (Photo) photoData.findById(id);
    }
}
