package com.sleeved.looter.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateUtilTest {
  @Test
  void parseDate_shouldReturnNull_whenDateStringIsNull() {
    LocalDate result = DateUtil.parseDate(null, "yyyy/MM/dd");
    assertThat(result).isNull();
  }

  @Test
  void parseDate_shouldReturnNull_whenDateStringIsEmpty() {
    LocalDate result = DateUtil.parseDate("", "yyyy/MM/dd");
    assertThat(result).isNull();
  }

  @Test
  void parseDate_shouldReturnCorrectDate_whenDateStringIsValid() {
    LocalDate result = DateUtil.parseDate("2025/05/26", "yyyy/MM/dd");
    assertThat(result).isEqualTo(LocalDate.of(2025, 5, 26));
  }

  @Test
  void parseDateTime_shouldReturnNull_whenDateTimeStringIsNull() {
    LocalDateTime result = DateUtil.parseDateTime(null, "yyyy/MM/dd HH:mm:ss");
    assertThat(result).isNull();
  }

  @Test
  void parseDateTime_shouldReturnNull_whenDateTimeStringIsEmpty() {
    LocalDateTime result = DateUtil.parseDateTime("", "yyyy/MM/dd HH:mm:ss");
    assertThat(result).isNull();
  }

  @Test
  void parseDateTime_shouldReturnCorrectDateTime_whenDateTimeStringIsValid() {
    LocalDateTime result = DateUtil.parseDateTime("2025/05/26 15:30:45", "yyyy/MM/dd HH:mm:ss");
    assertThat(result).isEqualTo(LocalDateTime.of(2025, 5, 26, 15, 30, 45));
  }

  @Test
  void normalizeYearInDateTime_shouldReturnSameString_whenFormatDoesNotMatch() {
    String dateTime = "2025-05-26 15:30:45";
    String result = DateUtil.normalizeYearInDateTime(dateTime);
    assertThat(result).isEqualTo(dateTime);
  }

  @Test
  void normalizeYearInDateTime_shouldNotChangeYear_whenYearHasFourDigits() {
    String result = DateUtil.normalizeYearInDateTime("2025/05/26 15:30:45");
    assertThat(result).isEqualTo("2025/05/26 15:30:45");
  }

  @Test
  void normalizeYearInDateTime_shouldNormalizeYear_whenYearHasOneDigit() {
    String result = DateUtil.normalizeYearInDateTime("5/05/26 15:30:45");
    assertThat(result).isEqualTo("2025/05/26 15:30:45");
  }

  @Test
  void normalizeYearInDateTime_shouldNormalizeYear_whenYearHasTwoDigits() {
    String result = DateUtil.normalizeYearInDateTime("25/05/26 15:30:45");
    assertThat(result).isEqualTo("2025/05/26 15:30:45");
  }

  @Test
  void normalizeYearInDateTime_shouldNormalizeYear_whenYearHasThreeDigits() {
    String result = DateUtil.normalizeYearInDateTime("025/05/26 15:30:45");
    assertThat(result).isEqualTo("2025/05/26 15:30:45");
  }
}
