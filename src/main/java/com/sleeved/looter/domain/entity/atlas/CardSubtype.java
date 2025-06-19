package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Card_Subtype")
@Data
public class CardSubtype {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;

  @ManyToOne
  @JoinColumn(name = "subtype_id", nullable = false)
  private Subtype subtype;
}
