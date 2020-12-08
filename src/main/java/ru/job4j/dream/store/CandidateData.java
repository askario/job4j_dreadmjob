package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Log4j2
public class CandidateData implements DbStore<Candidate> {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();
    private final DbStore photoData = PhotoData.instOf();
    private final DbStore cityData = CityData.instOf();

    private final String SELECT_ALL = "SELECT * FROM CANDIDATE;";
    private final String CREATE_CANDIDATE = "INSERT INTO CANDIDATE(name, photo_id, city_id) VALUES (?,?);";
    private final String SELECT_WHERE = "SELECT * FROM CANDIDATE WHERE id = (?);";
    private final String UPDATE_CANDIDATE = "UPDATE CANDIDATE SET name = ?, photo_id = ?, city_id = ? WHERE id = ?;";
    private final String DELETE_CANDIDATE = "DELETE FROM PHOTO WHERE id = (?)";

    private static final class Lazy {
        private static final DbStore INST = new CandidateData();
    }

    public static DbStore instOf() {
        return CandidateData.Lazy.INST;
    }

    @Override
    public Collection<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("id"), it.getString("name"), (Photo) photoData.findById(it.getInt("photo_id")), (City) cityData.findById(it.getInt("city_id"))));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return candidates;
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    @Override
    public Candidate findById(int id) {
        Candidate candidate = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_WHERE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ps.execute();

            try (ResultSet it = ps.getResultSet()) {
                if (it.next()) {
                    candidate = new Candidate(it.getInt("id"), it.getString("name"), new Photo(it.getInt("photo_id"), null),new City(it.getInt("city_id"),null));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return candidate;
    }

    @Override
    public void delete(Candidate candidate) {
        Photo photo = candidate.getPhoto();
        if (photo != null) {
            photoData.delete(photo);
        }

        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_CANDIDATE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            log.error("Error while delete candidate: ", e);
        }
    }

    @Override
    public void update(Candidate candidate) {
        Optional<Photo> photoOpt = Optional.ofNullable(candidate.getPhoto());
        photoOpt.ifPresent(ph -> photoData.save(ph));

        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_CANDIDATE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());

            if (photoOpt.isPresent())
                ps.setInt(2, photoOpt.get().getId());
            else
                ps.setNull(2, Types.INTEGER);

            ps.setInt(3, candidate.getCity().getId());

            ps.setInt(4, candidate.getId());

            ps.execute();
        } catch (Exception e) {
            log.error("Error while update candidate: ", e);
        }
    }

    private Candidate create(Candidate candidate) {
        Optional<Photo> photoOpt = Optional.ofNullable(candidate.getPhoto());
        photoOpt.ifPresent(ph -> photoData.save(ph));

        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(CREATE_CANDIDATE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());

            if (photoOpt.isPresent())
                ps.setInt(2, photoOpt.get().getId());
            else
                ps.setNull(2, Types.INTEGER);

            ps.setInt(3, candidate.getCity().getId());

            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            log.error("Error during candidate creation: ", e);
        }
        return candidate;
    }
}
