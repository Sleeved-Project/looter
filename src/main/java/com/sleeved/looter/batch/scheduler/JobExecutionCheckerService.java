package com.sleeved.looter.batch.scheduler;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobExecutionCheckerService {
    
    private final JdbcTemplate jdbcTemplate;

    public JobExecutionCheckerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Verify if a job has been completed since a specific date
     */
    public boolean isJobCompletedOnDate(String jobName, LocalDate date) {
        String sql = """
            SELECT bje.START_TIME
            FROM BATCH_JOB_EXECUTION bje
            LEFT JOIN BATCH_JOB_INSTANCE bji ON bje.JOB_INSTANCE_ID = bji.JOB_INSTANCE_ID
            WHERE bji.JOB_NAME = ?
            AND bje.STATUS = 'COMPLETED'
            ORDER BY bje.START_TIME DESC
            LIMIT 1
            """;
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Timestamp startTime = rs.getTimestamp("START_TIME");
                return startTime.toLocalDateTime().toLocalDate().isEqual(date);
            }
            return false;
        }, jobName);
    }

}
