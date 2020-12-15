package ru.job4j.dream.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.List;

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
        Session session = sf.openSession();
        session.beginTransaction();
        List<Post> posts = session.createQuery("from ru.job4j.dream.model.Post").list();
        session.getTransaction().commit();
        session.close();
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
        Session session = sf.openSession();
        session.beginTransaction();
        Post post = session.get(Post.class, id);
        session.getTransaction().commit();
        session.close();
        return post;
    }

    @Override
    public void delete(Post post) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.delete(post);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(Post post) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.update(post);
        session.getTransaction().commit();
        session.close();
    }

    private Post create(Post post) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(post);
        session.getTransaction().commit();
        session.close();
        return post;
    }
}
