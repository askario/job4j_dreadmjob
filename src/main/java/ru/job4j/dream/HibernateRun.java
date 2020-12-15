package ru.job4j.dream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.dream.model.Post;

import java.util.List;

public class HibernateRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();

        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Post post = create(new Post("Java Job"), sf);
            System.out.println(post);
            post.setName("Java full stack job");
            update(post, sf);
            System.out.println(post);
            Post rsl = findById(post.getId(), sf);
            System.out.println(rsl);
            delete(rsl.getId(), sf);

            List<Post> posts = findAll(sf);
            for (Post item : posts) {
                System.out.println(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static Post create(Post post, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(post);
        session.getTransaction().commit();
        session.close();
        return post;
    }

    public static void update(Post post, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.update(post);
        session.getTransaction().commit();
        session.close();
    }

    public static Post findById(int id, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Post result = session.get(Post.class, id);
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public static void delete(int id, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Post post = new Post(null);
        post.setId(id);
        session.delete(post);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Post> findAll(SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from ru.job4j.dream.model.Post").list();
        session.getTransaction().commit();
        session.close();
        return result;
    }
}
