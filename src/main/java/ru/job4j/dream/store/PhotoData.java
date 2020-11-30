package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class PhotoData implements DbStore<Photo> {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();

    private final String SELECT_ALL = "SELECT * FROM PHOTO";
    private final String SELECT_WHERE = "SELECT * FROM photo WHERE id = (?)";
    private final String CREATE_PHOTO = "INSERT INTO PHOTO(name) VALUES (?)";
    private final String UPDATE_PHOTO = "UPDATE PHOTO SET name = (?) WHERE id = (?)";
    private final String DELETE_PHOTO = "DELETE FROM PHOTO WHERE id = (?)";

    private static final class Lazy {
        private static final DbStore INST = new PhotoData();
    }

    public static DbStore instOf() {
        return PhotoData.Lazy.INST;
    }

    @Override
    public Collection<Photo> findAll() {
        List<Photo> photos = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    photos.add(new Photo(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching all photos: ", e);
        }
        return photos;
    }

    @Override
    public void save(Photo photo) {
        if (photo.getId() == 0) {
            create(photo);
        } else {
            update(photo);
        }
    }

    @Override
    public Photo findById(int id) {
        Photo photo = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_WHERE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ps.execute();

            try (ResultSet it = ps.getResultSet()) {
                if (it.next()) {
                    photo = new Photo(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return photo;
    }

    @Override
    public void delete(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_PHOTO, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, photo.getId());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while delete photo: ", e);
        }
    }

    @Override
    public void update(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_PHOTO, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo.getName());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while update photo: ", e);
        }
    }

    private Photo create(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(CREATE_PHOTO, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    photo.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            log.error("Error during photo creation: ", e);
        }
        return photo;
    }
}
