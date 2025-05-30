package com.sleeved.looter.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ParseUtilTest {

  @Test
  void parseIntegerListIntoStringList_shouldReturnEmptyList_whenInputListIsEmpty() {
    List<Integer> emptyList = Collections.emptyList();

    List<String> result = ParseUtil.parseIntegerListIntoStringList(emptyList);

    assertThat(result).isEmpty();
  }

  @Test
  void parseIntegerListIntoStringList_shouldReturnStringList_whenInputListContainsSingleInteger() {
    List<Integer> singleElementList = Collections.singletonList(42);

    List<String> result = ParseUtil.parseIntegerListIntoStringList(singleElementList);

    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("42");
  }

  @Test
  void parseIntegerListIntoStringList_shouldReturnStringList_whenInputListContainsMultipleIntegers() {
    List<Integer> multipleElementsList = Arrays.asList(1, 2, 3, 4, 5);

    List<String> result = ParseUtil.parseIntegerListIntoStringList(multipleElementsList);

    assertThat(result).hasSize(5);
    assertThat(result).containsExactly("1", "2", "3", "4", "5");
  }

  @Test
  void parseIntegerListIntoStringList_shouldReturnStringList_whenInputListContainsNegativeIntegers() {
    List<Integer> negativeElementsList = Arrays.asList(-10, -5, 0, 5, 10);

    List<String> result = ParseUtil.parseIntegerListIntoStringList(negativeElementsList);

    assertThat(result).hasSize(5);
    assertThat(result).containsExactly("-10", "-5", "0", "5", "10");
  }

  @Test
  void parseIntegerListIntoStringList_shouldThrowNullPointerException_whenInputListIsNull() {
    assertThatThrownBy(() -> ParseUtil.parseIntegerListIntoStringList(null))
        .isInstanceOf(NullPointerException.class);
  }
}