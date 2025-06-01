package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.repository.atlas.TcgPlayerReportingRepository;

@Service
public class TcgPlayerReportingService {

  private final TcgPlayerReportingRepository tcgPlayerReportingRepository;

  public TcgPlayerReportingService(TcgPlayerReportingRepository tcgPlayerReportingRepository) {
    this.tcgPlayerReportingRepository = tcgPlayerReportingRepository;
  }

  public TcgPlayerReporting getOrCreate(TcgPlayerReporting tcgPlayerReportingPrice) {
    return tcgPlayerReportingRepository.findByUpdatedAtAndCard(
        tcgPlayerReportingPrice.getUpdatedAt(), tcgPlayerReportingPrice.getCard())
        .orElseGet(() -> tcgPlayerReportingRepository.save(tcgPlayerReportingPrice));
  }
}