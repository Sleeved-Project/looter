
package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card_weakness")
@Data
public class CardWeakness {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;

  @ManyToOne
  @JoinColumn(name = "weakness_id", nullable = false)
  private Weakness weakness;
}
