package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class PostData implements DbStore<Post> {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();

    private final String SELECT_ALL = "SELECT * FROM POST";
    private final String CREATE_POST = "INSERT INTO POST(name) VALUES (?)";
    private final String UPDATE_POST = "UPDATE POST SET name = (?) WHERE id = (?)";
    private final String SELECT_WHERE = "SELECT * FROM POST WHERE id = (?)";
    private final String DELETE_POST = "DELETE FROM POST WHERE id = (?)";

    private static final class Lazy {
        private static final DbStore INST = new PostData();
    }

    public static DbStore instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching all posts: ", e);
        }
        return posts;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_WHERE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ps.execute();

            try (ResultSet it = ps.getResultSet()) {
                if (it.next()) {
                    post = new Post(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return post;
    }

    @Override
    public void delete(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, post.getId());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while delete post: ", e);
        }
    }

    @Override
    public void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while update post: ", e);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(CREATE_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            log.error("Error during post creation: ", e);
        }
        return post;
    }
}
