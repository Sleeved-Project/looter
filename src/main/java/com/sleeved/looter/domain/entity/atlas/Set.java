package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "`Set`")
@Data
public class Set {
  @Id
  @Column(nullable = false, unique = true)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String series;

  @Column(name = "printed_total", nullable = false)
  private int printedTotal;

  @Column(nullable = false)
  private int total;

  @Column(name = "ptcgo_code", nullable = true, length = 100)
  private String ptcgoCode;

  @Column(name = "release_date", nullable = false)
  private LocalDate releaseDate;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "image_symbol", nullable = false, columnDefinition = "mediumtext")
  private String imageSymbol;

  @Column(name = "image_logo", nullable = false, columnDefinition = "mediumtext")
  private String imageLogo;

  @ManyToOne
  @JoinColumn(name = "leaglity_id", nullable = false)
  private Legalities legalities;
}