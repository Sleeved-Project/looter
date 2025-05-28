package com.sleeved.looter.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
  public static LocalDate parseDate(String dateString, String format) {
    if (dateString == null || dateString.isEmpty()) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateString, formatter);
  }

  public static LocalDateTime parseDateTime(String dateTimeString, String format) {
    if (dateTimeString == null || dateTimeString.isEmpty()) {
      return null;
    }

    dateTimeString = normalizeYearInDateTime(dateTimeString);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(dateTimeString, formatter);
  }

  protected static String normalizeYearInDateTime(String dateTimeString) {
    Pattern pattern = Pattern.compile("^(\\d{1,3})/(\\d{1,2})/(\\d{1,2})\\s+(.*)$");
    Matcher matcher = pattern.matcher(dateTimeString);

    if (matcher.find()) {
      String year = matcher.group(1);
      String month = matcher.group(2);
      String day = matcher.group(3);
      String time = matcher.group(4);

      if (year.length() < 4) {
        if (year.length() == 1) {
          year = "202" + year; // 5 -> 2025
        } else if (year.length() == 2) {
          year = "20" + year; // 25 -> 2025
        } else if (year.length() == 3) {
          year = "2" + year; // 025 -> 2025
        }
      }

      return year + "/" + month + "/" + day + " " + time;
    }

    return dateTimeString;
  }

}
