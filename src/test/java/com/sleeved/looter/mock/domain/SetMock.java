package com.sleeved.looter.mock.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Set;

public class SetMock {

  public static Set createMockSet(String id, String name, String series, Integer printedTotal,
      Integer total, String ptcgoCode, String imageSymbole, String imageLogo, Legalities legalities) {
    Set set = new Set();
    set.setId(id);
    set.setName(name);
    set.setSeries(series);
    set.setPrintedTotal(printedTotal);
    set.setTotal(total);
    set.setPtcgoCode(ptcgoCode);
    set.setReleaseDate(LocalDate.now());
    set.setUpdatedAt(LocalDateTime.now());
    set.setImageSymbol(imageSymbole);
    set.setImageLogo(imageLogo);
    set.setLegalities(legalities);
    return set;
  }
}