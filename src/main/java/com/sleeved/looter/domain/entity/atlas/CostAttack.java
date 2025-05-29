package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Cost_Attack")
@Data
public class CostAttack {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, unique = true)
  private Integer id;

  @Column(nullable = false)
  private Integer cost;

  @Column(name = "is_free", nullable = false)
  private Boolean isFree;

  @ManyToOne
  @JoinColumn(name = "type_id", nullable = true)
  private Type type;

  @ManyToOne
  @JoinColumn(name = "attack_id", nullable = false)
  private Attack attack;
}