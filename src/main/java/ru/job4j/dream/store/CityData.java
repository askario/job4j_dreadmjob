package ru.job4j.dream.store;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class CityData implements DbStore<City> {
    private final BasicDataSource pool = PsqlSetup.instOf().getPool();

    private final String SELECT_ALL = "SELECT * FROM CITY";
    private final String SELECT_WHERE = "SELECT * FROM CITY WHERE id = (?);";

    private static final class Lazy {
        private static final CityData INST = new CityData();
    }

    public static CityData instOf() {
        return CityData.Lazy.INST;
    }

    @Override
    public Collection<City> findAll() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(new City(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching all cities: ", e);
        }
        return cities;
    }

    @Override
    public void save(City city) {

    }

    @Override
    public City findById(int id) {
        City city = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_WHERE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ps.execute();

            try (ResultSet it = ps.getResultSet()) {
                if (it.next()) {
                    city = new City(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            log.error("Error message: ", e);
        }
        return city;
    }

    @Override
    public void delete(City city) {

    }

    @Override
    public void update(City city) {

    }
}
