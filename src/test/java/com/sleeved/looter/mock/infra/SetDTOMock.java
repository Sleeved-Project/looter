package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.SetDTO;

public class SetDTOMock {

  public static SetDTO createMockSetDTO(String id, String name, String series, Integer printedTotal,
      Integer total, String ptcgoCode, String releaseDate,
      String updatedAt, String symbolUrl, String logoUrl) {
    SetDTO dto = new SetDTO();
    dto.setId(id);
    dto.setName(name);
    dto.setSeries(series);
    dto.setPrintedTotal(printedTotal);
    dto.setTotal(total);
    dto.setPtcgoCode(ptcgoCode);
    dto.setReleaseDate(releaseDate);
    dto.setUpdatedAt(updatedAt);
    dto.setImages(ImageDTOMock.createMockImageDTO(symbolUrl, logoUrl));
    return dto;
  }
}