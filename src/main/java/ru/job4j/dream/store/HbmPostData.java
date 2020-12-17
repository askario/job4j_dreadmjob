package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.dream.model.Post;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
public class HbmPostData implements DbStore<Post>, AutoCloseable {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();

    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata()
            .buildSessionFactory();

    private static final class Lazy {
        private static final HbmPostData INST = new HbmPostData();
    }

    public static HbmPostData instOf() {
        return HbmPostData.Lazy.INST;
    }


    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Override
    public Collection<Post> findAll() {
        Collection<Post> posts = new ArrayList<>();
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            posts = session.createQuery("from ru.job4j.dream.model.Post").list();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while trying to fetch all posts: ", e);
            sf.getCurrentSession().getTransaction().rollback();
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
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            post = session.get(Post.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error in findById post: ", e);
            sf.getCurrentSession().getTransaction().rollback();
        }
        return post;
    }

    @Override
    public void delete(Post post) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.delete(post);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while trying to delete post: ", e);
            sf.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.update(post);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while updating post: ", e);
            sf.getCurrentSession().getTransaction().rollback();
        }
    }

    private Post create(Post post) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(post);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while creating new post: ", e);
            sf.getCurrentSession().getTransaction().rollback();
        }
        return post;
    }
}
