package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE from user_roles WHERE user_id=?", user.getId());
        }
        saveRoles(user.getId(), new ArrayList(user.getRoles()));
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return updateRoles(DataAccessUtils.singleResult(users));
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return updateRoles(DataAccessUtils.singleResult(users));
    }

    @Override
    public List<User> getAll() {
        final Map<Integer, User> mapUsers = new LinkedHashMap<>();
        String sql = "SELECT * FROM users LEFT JOIN user_roles ON id = user_id ORDER BY name, email";
        jdbcTemplate.query(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                Role role = Role.valueOf(rs.getString("role"));
                User user = ROW_MAPPER.mapRow(rs, rowNum);
                mapUsers.computeIfAbsent(user.getId(), id -> {
                    user.setRoles(EnumSet.of(role));
                    return user;
                }).getRoles().add(role);
                return user;
            }
        });
        return new ArrayList<>(mapUsers.values());
    }

    private User updateRoles(User user) {
        if (user == null) return null;
        user.setRoles(jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", new Object[]{user.getId()}, new ResultSetExtractor<Collection<Role>>() {
                    @Override
                    public Collection<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        EnumSet<Role> roles = EnumSet.noneOf(Role.class);
                        while (rs.next()) {
                            roles.add(Role.valueOf(rs.getString(2)));
                        }
                        return roles;
                    }
                }
        ));
        return user;
    }

    private void saveRoles(Integer id, List<Role> roles) {

        String sql = "insert into user_roles(role, user_id) values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, roles.get(i).name());
                ps.setInt(2, id);
            }

            @Override
            public int getBatchSize() {
                return roles.size();
            }

        });
    }

}
