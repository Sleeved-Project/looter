package com.sleeved.looter.domain.repository.staging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.staging.StagingPrice;

@Repository
public interface StagingPriceRepository extends JpaRepository<StagingPrice, String> {
}
