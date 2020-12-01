package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class UsersData implements DbStore<User> {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();

    private final String SELECT_ALL = "SELECT * FROM USERS";
    private final String CREATE_USER = "INSERT INTO USERS(name, email, password) VALUES (?, ?, ?)";
    private final String UPDATE_USER = "UPDATE USERS SET name = (?) WHERE id = (?)";
    private final String SELECT_WHERE = "SELECT * FROM USERS WHERE id = (?)";
    private final String DELETE_USER = "DELETE FROM USERS WHERE id = (?)";

    private static final class Lazy {
        private static final DbStore INST = new UsersData();
    }

    public static DbStore instOf() {
        return UsersData.Lazy.INST;
    }

    @Override
    public Collection<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(it.getInt("id"), it.getString("name"), it.getString("email"), it.getString("password")));
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching all users: ", e);
        }
        return users;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    @Override
    public User findById(int id) {
        User user = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_WHERE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ps.execute();

            try (ResultSet it = ps.getResultSet()) {
                if (it.next()) {
                    user = new User(it.getInt("id"), it.getString("name"), it.getString("email"), it.getString("password"));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return user;
    }

    @Override
    public void delete(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_USER, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, user.getId());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while delete user: ", e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_USER, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setInt(2, user.getId());

            ps.execute();
        } catch (Exception e) {
            log.error("Error while update user: ", e);
        }
    }

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(CREATE_USER, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            log.error("Error during user creation: ", e);
        }
        return user;
    }
}
