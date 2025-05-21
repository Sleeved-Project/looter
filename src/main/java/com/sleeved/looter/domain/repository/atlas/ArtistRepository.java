package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
  Optional<Artist> findByName(String name);
}
