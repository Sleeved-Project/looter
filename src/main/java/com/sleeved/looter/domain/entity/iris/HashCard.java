package com.sleeved.looter.domain.entity.iris;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "card_hash")
@Data
public class HashCard {

  @Id
  @Column(nullable = false, unique = true, length = 100)
  private String id;

  @Column(nullable = false)
  private String hash;
}