package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Resistance")
@Data
public class Resistance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 20)
  private String value;

  @ManyToOne()
  @JoinColumn(name = "type_id", nullable = false)
  private Type type;
}