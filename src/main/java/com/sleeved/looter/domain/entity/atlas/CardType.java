package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Card_Type")
@Data
public class CardType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;

  @ManyToOne
  @JoinColumn(name = "type_id", nullable = false)
  private Type type;
}
