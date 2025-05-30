package com.sleeved.looter.common.util;

import java.util.List;

public class ParseUtil {
  public static List<String> parseIntegerListIntoStringList(List<Integer> integerList) {
    return integerList.stream().map(String::valueOf).toList();
  }
}
