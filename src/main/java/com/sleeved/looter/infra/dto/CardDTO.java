package com.sleeved.looter.infra.dto;

import java.util.List;

import lombok.Data;

@Data
public class CardDTO {
  private String id;
  private String name;
  private String supertype;
  private List<String> subtypes;
  private String level;
  private String hp;
  private List<String> types;
  private List<String> evolvesTo;
  private List<AbilityDTO> abilities;
  private List<AttackDTO> attacks;
  private List<WeaknessDTO> weaknesses = List.of();
  private List<ResistanceDTO> resistances = List.of();
  private List<String> retreatCost;
  private Integer convertedRetreatCost;
  private SetDTO set;
  private String number;
  private String artist;
  private String rarity;
  private String flavorText;
  private List<Integer> nationalPokedexNumbers;
  private LegalitiesDTO legalities;
  private ImageDTO images;
  private TcgPlayerDTO tcgplayer;
  private CardMarketDTO cardmarket;
}
