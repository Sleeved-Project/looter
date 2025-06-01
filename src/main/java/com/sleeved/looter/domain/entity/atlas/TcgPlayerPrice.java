package com.sleeved.looter.domain.entity.atlas;

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
@Table(name = "Tcg_Player_Price")
@Data
public class TcgPlayerPrice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "type", nullable = false, length = 100)
  private String type; // normal, holofoil, reverseHolofoil, 1stEditionHolofoil and 1stEditionNormal

  @Column(name = "low")
  private Double low;

  @Column(name = "mid")
  private Double mid;

  @Column(name = "high")
  private Double high;

  @Column(name = "market")
  private Double market;

  @Column(name = "direct_low")
  private Double directLow;

  @ManyToOne()
  @JoinColumn(name = "tcg_player_reporting_id", nullable = false)
  private TcgPlayerReporting tcgPlayerReporting;
}
