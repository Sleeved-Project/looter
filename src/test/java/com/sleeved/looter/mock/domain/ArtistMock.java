package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Artist;

public class ArtistMock {
  public static Artist createMockArtist(String name) {
    Artist artist = new Artist();
    artist.setName(name);
    return artist;
  }

  public static Artist createMockArtistSavedInDb(int id, String name) {
    Artist artist = new Artist();
    artist.setId(id);
    artist.setName(name);
    return artist;
  }
}