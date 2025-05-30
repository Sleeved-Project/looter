package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Card")
@Data
public class Card {
  @Id
  @Column(nullable = false, unique = true, length = 100)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 100)
  private String supertype;

  @Column(length = 20, nullable = true)
  private String level;

  @Column(length = 20, nullable = true)
  private String hp;

  @Column(name = "evolves_from", nullable = true)
  private String evolvesFrom;

  @Column(name = "evolves_to", nullable = true)
  private String evolvesTo;

  @Column(name = "converted_retreat_cost", nullable = false)
  private int convertedRetreatCost;

  @Column(nullable = true, length = 20)
  private String number;

  @Column(name = "image_large", nullable = false, columnDefinition = "mediumtext")
  private String imageLarge;

  @Column(name = "image_small", nullable = false, columnDefinition = "mediumtext")
  private String imageSmall;

  @Column(name = "flavor_text", columnDefinition = "mediumtext", nullable = true)
  private String flavorText;

  @Column(name = "national_pokedex_numbers", length = 20, nullable = true)
  private String nationalPokedexNumbers;

  @ManyToOne
  @JoinColumn(name = "artist_id", nullable = false)
  private Artist artist;

  @ManyToOne
  @JoinColumn(name = "rarity_id", nullable = false)
  private Rarity rarity;

  @ManyToOne
  @JoinColumn(name = "set_id", nullable = false)
  private Set set;

  @ManyToOne
  @JoinColumn(name = "legality_id", nullable = false)
  private Legalities legalities;
}