package com.sleeved.looter.domain.entity.staging;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Staging_Price")
@Data
public class StagingPrice {

  @Id
  private String id;

  @Column(columnDefinition = "json", nullable = false)
  private String payload;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "batch_id")
  private Long batchId;
}
