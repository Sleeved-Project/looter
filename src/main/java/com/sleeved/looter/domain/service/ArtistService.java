package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.repository.atlas.ArtistRepository;

@Service
public class ArtistService {
  private final ArtistRepository artistRepository;

  public ArtistService(ArtistRepository artistRepository) {
    this.artistRepository = artistRepository;
  }

  public Artist getOrCreate(Artist artist) {
    return artistRepository.findByName(artist.getName())
        .orElseGet(() -> artistRepository.save(artist));
  }
}
