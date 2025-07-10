package com.sleeved.looter.batch.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

@ExtendWith(MockitoExtension.class)
public class JobExecutionCheckerServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JobExecutionCheckerService jobExecutionCheckerService;

    @Test
    void isJobCompletedOnDate_shouldReturnTrue_whenJobCompletedOnSpecificDate() throws SQLException {
        LocalDate targetDate = LocalDate.of(2024, 1, 15);
        Timestamp timestamp = Timestamp.valueOf(targetDate.atTime(10, 30));

        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ResultSetExtractor<Boolean>>any(), anyString()))
            .thenAnswer(invocation -> {
                ResultSet rs = mock(ResultSet.class);
                when(rs.next()).thenReturn(true);
                when(rs.getTimestamp("START_TIME")).thenReturn(timestamp);
                
                ResultSetExtractor<Boolean> extractor = invocation.getArgument(1);
                return extractor.extractData(rs);
            });

        boolean result = jobExecutionCheckerService.isJobCompletedOnDate("testJob", targetDate);

        assertThat(result).isTrue();
        verify(jdbcTemplate).query(anyString(), ArgumentMatchers.<ResultSetExtractor<Boolean>>any(), eq("testJob"));
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenJobCompletedOnDifferentDate() throws SQLException {
        LocalDate targetDate = LocalDate.of(2024, 1, 15);
        LocalDate differentDate = LocalDate.of(2024, 1, 14);
        Timestamp timestamp = Timestamp.valueOf(differentDate.atTime(10, 30));

        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ResultSetExtractor<Boolean>>any(), anyString()))
            .thenAnswer(invocation -> {
                ResultSet rs = mock(ResultSet.class);
                when(rs.next()).thenReturn(true);
                when(rs.getTimestamp("START_TIME")).thenReturn(timestamp);
                
                ResultSetExtractor<Boolean> extractor = invocation.getArgument(1);
                return extractor.extractData(rs);
            });

        boolean result = jobExecutionCheckerService.isJobCompletedOnDate("testJob", targetDate);

        assertThat(result).isFalse();
    }

    @Test
    void isJobCompletedOnDate_shouldReturnFalse_whenNoJobFound() throws SQLException {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<ResultSetExtractor<Boolean>>any(), anyString()))
            .thenAnswer(invocation -> {
                ResultSet rs = mock(ResultSet.class);
                when(rs.next()).thenReturn(false);
                
                ResultSetExtractor<Boolean> extractor = invocation.getArgument(1);
                return extractor.extractData(rs);
            });

        boolean result = jobExecutionCheckerService.isJobCompletedOnDate("testJob", LocalDate.now());

        assertThat(result).isFalse();
    }
}