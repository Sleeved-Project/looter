package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;
import com.sleeved.looter.mock.infra.TcgPlayerPriceDTOMock;

class TcgPlayerPriceMapperTest {

  private TcgPlayerPriceMapper mapper;
  private TcgPlayerPriceDTO priceDTO;
  private Card card;
  private TcgPlayerReporting reporting;

  @BeforeEach
  void setUp() {
    mapper = new TcgPlayerPriceMapper();
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    reporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(1L, "https://tcgplayer.com/pikachu",
        LocalDate.now(), card);
    priceDTO = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(1.0, 2.0, 3.0, 4.0, 5.0);
  }

  @Test
  void toEntity_shouldReturnNull_whenPriceDTOIsNull() {
    TcgPlayerPriceDTO nullDTO = null;

    TcgPlayerPrice result = mapper.toEntity(nullDTO, "normal", reporting);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNull_whenReportingIsNull() {
    TcgPlayerReporting nullReporting = null;

    TcgPlayerPrice result = mapper.toEntity(priceDTO, "normal", nullReporting);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldMapAllFields_whenInputsAreValid() {
    String priceType = "holofoil";

    TcgPlayerPrice result = mapper.toEntity(priceDTO, priceType, reporting);

    assertThat(result).isNotNull();
    assertThat(result.getTcgPlayerReporting()).isEqualTo(reporting);
    assertThat(result.getType()).isEqualTo(priceType);
    assertThat(result.getLow()).isEqualTo(1.0);
    assertThat(result.getMid()).isEqualTo(2.0);
    assertThat(result.getHigh()).isEqualTo(3.0);
    assertThat(result.getMarket()).isEqualTo(4.0);
    assertThat(result.getDirectLow()).isEqualTo(5.0);
  }

  @Test
  void toEntity_shouldHandleNullValues_forPrices() {
    TcgPlayerPriceDTO dtoWithNulls = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(null, 2.0, null, 4.0, null);

    TcgPlayerPrice result = mapper.toEntity(dtoWithNulls, "normal", reporting);

    assertThat(result).isNotNull();
    assertThat(result.getLow()).isNull();
    assertThat(result.getMid()).isEqualTo(2.0);
    assertThat(result.getHigh()).isNull();
    assertThat(result.getMarket()).isEqualTo(4.0);
    assertThat(result.getDirectLow()).isNull();
  }

  @Test
  void toEntity_shouldPreserveEmptyString_forTypeWhenProvided() {
    String emptyType = "";

    TcgPlayerPrice result = mapper.toEntity(priceDTO, emptyType, reporting);

    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo("");
  }

  @Test
  void toEntity_shouldHandleNullString_forType() {
    String nullType = null;

    TcgPlayerPrice result = mapper.toEntity(priceDTO, nullType, reporting);

    assertThat(result).isNotNull();
    assertThat(result.getType()).isNull();
  }
}