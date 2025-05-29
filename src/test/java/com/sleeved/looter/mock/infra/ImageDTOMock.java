package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.ImageDTO;

public class ImageDTOMock {

  public static ImageDTO createMockImageDTO(String symbolUrl, String logoUrl) {
    ImageDTO imageDTO = new ImageDTO();
    imageDTO.setSymbol(symbolUrl);
    imageDTO.setLogo(logoUrl);
    return imageDTO;
  }
}