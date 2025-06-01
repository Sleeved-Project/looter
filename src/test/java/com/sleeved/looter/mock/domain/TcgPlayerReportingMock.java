package com.sleeved.looter.mock.domain;

import java.time.LocalDate;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

public class TcgPlayerReportingMock {

  public static TcgPlayerReporting createMockTcgPlayerReporting(Long id, String url, LocalDate updatedAt, Card card) {
    TcgPlayerReporting reporting = new TcgPlayerReporting();
    reporting.setId(id);
    reporting.setUrl(url);
    reporting.setUpdatedAt(updatedAt);
    reporting.setCard(card);
    return reporting;
  }

  public static TcgPlayerReporting createMockTcgPlayerReportingSavedInDb(Long id, String url, LocalDate updatedAt,
      Card card) {
    return createMockTcgPlayerReporting(id, url, updatedAt, card);
  }
}