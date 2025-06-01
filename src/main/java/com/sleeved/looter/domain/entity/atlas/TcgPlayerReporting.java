package com.sleeved.looter.domain.entity.atlas;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Tcg_Player_Reporting")
@Data
public class TcgPlayerReporting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "mediumtext")
  private String url;

  @Column(name = "updated_at", nullable = false)
  private LocalDate updatedAt;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;
}
