package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Card_Attack")
@Data
public class CardAttack {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;

  @ManyToOne
  @JoinColumn(name = "attack_id", nullable = false)
  private Attack attack;
}
