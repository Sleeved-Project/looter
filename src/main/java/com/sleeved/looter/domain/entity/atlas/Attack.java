package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Attack")
@Data
public class Attack {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = true)
  private String text;

  @Column(nullable = true)
  private String damage;

  @Column(nullable = false)
  private Integer convertedEnergyCost;

}
