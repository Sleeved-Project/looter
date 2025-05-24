package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Artist;

@Component
public class ArtistMapper {
  public Artist toEntity(String artistName) {
    Artist artist = new Artist();
    if (artistName != null && !artistName.isBlank()) {
      artist.setName(artistName.trim());
    } else {
      artist.setName("UNKNOWN");
    }
    return artist;
  }
}
