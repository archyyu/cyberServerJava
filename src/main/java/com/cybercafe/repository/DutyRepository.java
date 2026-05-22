package com.cybercafe.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Repository
public class DutyRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall generateDutyDataCall;
    private final SimpleJdbcCall dutyDataSaveNewCall;
    private final SimpleJdbcCall getDutyDateCall;

    public DutyRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.generateDutyDataCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("generateDutyData")
                .declareParameters(
                        new SqlParameter("@gid_", Types.INTEGER),
                        new SqlOutParameter("@result", Types.INTEGER)
                );

        this.dutyDataSaveNewCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("dutyDataSaveNew");
                // Parameters dynamically bound, mimicking the dynamic bindings in DutyDao.cs

        this.getDutyDateCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("getDutyDate")
                .declareParameters(
                        new SqlParameter("@gid_", Types.INTEGER),
                        new SqlOutParameter("@result", Types.INTEGER)
                );
    }

    public Map<String, Object> generateDutyData(int gid) {
        return generateDutyDataCall.execute(Map.of("@gid_", gid));
    }

    public Map<String, Object> dutyDataSaveNew(Map<String, Object> inParams) {
        return dutyDataSaveNewCall.execute(inParams);
    }

    public Map<String, Object> getDutyDate(int gid) {
        return getDutyDateCall.execute(Map.of("@gid_", gid));
    }

    public void updateDutyId(long onlineId) {
        String sql = "UPDATE seq_duty SET id = ? WHERE id < ?";
        jdbcTemplate.update(sql, onlineId, onlineId);
    }
}
