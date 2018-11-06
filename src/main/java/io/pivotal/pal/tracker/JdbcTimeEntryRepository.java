package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    insertSql,
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        try {
            Map<String, Object> foundTimeEntry = jdbcTemplate.queryForMap("SELECT * FROM time_entries WHERE id = ?", id);
            return toTimeEntry(foundTimeEntry);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String updateSql = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours(), id);
        return find(id);
    }

    @Override
    public List<TimeEntry> list() {
        List<Map<String, Object>> foundTimeEntries = jdbcTemplate.queryForList("SELECT * FROM time_entries");

        return foundTimeEntries.stream().map(this::toTimeEntry).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    private TimeEntry toTimeEntry(Map<String, Object> foundTimeEntry) {
        Long timeEntryId = (Long) foundTimeEntry.get("id");
        long timeEntryProjectId = (long) foundTimeEntry.get("project_id");
        long timeEntryUserId = (long) foundTimeEntry.get("user_id");
        LocalDate date = ((Date) foundTimeEntry.get("date")).toLocalDate();
        int hours = (int) foundTimeEntry.get("hours");

        return new TimeEntry(timeEntryId, timeEntryProjectId, timeEntryUserId, date, hours);
    }
}
