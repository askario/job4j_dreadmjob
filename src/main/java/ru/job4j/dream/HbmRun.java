package ru.job4j.dream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.dream.model.Post;

import java.sql.Timestamp;

public class HbmRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();

        try{
            SessionFactory sf = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

            Session session = sf.openSession();
            session.beginTransaction();

            Post post  = Post.of("Java Junior Developer","Java,Sql",new Timestamp(1459510232000L));
            session.save(post);

            Post post2  = Post.of("Frontend Developer","JS,HTML,CSS",new Timestamp(1653810232000L));
            session.save(post2);

            session.getTransaction().commit();
            session.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
