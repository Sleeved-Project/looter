package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Weakness")
@Data
public class Weakness {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 20)
  private String value;

  @ManyToOne()
  @JoinColumn(name = "type_id", nullable = false)
  private Type type;
}