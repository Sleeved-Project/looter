package com.sleeved.looter.infra.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SetDTO {
  private String id;
  private String name;
  private String series;
  private int printedTotal;
  private int total;
  private Map<String, String> legalities;
  private String ptcgoCode;
  private String releaseDate;
  private String updatedAt;
  private ImageDTO images;
}
