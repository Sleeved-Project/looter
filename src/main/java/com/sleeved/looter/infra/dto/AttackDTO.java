package com.sleeved.looter.infra.dto;

import java.util.List;

import lombok.Data;

@Data
public class AttackDTO {
  private String name;
  private List<String> cost;
  private int convertedEnergyCost;
  private String damage;
  private String text;
}
