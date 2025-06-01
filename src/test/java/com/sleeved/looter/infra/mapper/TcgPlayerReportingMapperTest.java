package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.common.util.DateUtil;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.infra.dto.TcgPlayerDTO;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.infra.TcgPlayerDTOMock;
import com.sleeved.looter.mock.infra.TcgPlayerPriceDTOMock;

@ExtendWith(MockitoExtension.class)
public class TcgPlayerReportingMapperTest {

  @InjectMocks
  private TcgPlayerReportingMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    String url = "https://tcgplayer.com/card123";
    String updatedAt = "2023/05/15";
    TcgPlayerDTO tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTO(url, updatedAt);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    TcgPlayerReporting result = mapper.toEntity(tcgPlayerDTO, card);

    assertThat(result).isNotNull();
    assertThat(result.getUrl()).isEqualTo(url);
    assertThat(result.getUpdatedAt()).isEqualTo(DateUtil.parseDate(updatedAt, Constantes.STAGING_CARD_DATE_FORMAT));
    assertThat(result.getCard()).isEqualTo(card);
  }

  @Test
  void toEntity_shouldMapDTOWithPrices() {
    String url = "https://tcgplayer.com/card123";
    String updatedAt = "2023/05/15";

    Map<String, TcgPlayerPriceDTO> prices = new HashMap<>();
    TcgPlayerPriceDTO normalPrice = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(
        5.0, 7.5, 10.0, 8.0, 6.0);
    TcgPlayerPriceDTO holoPrice = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(
        15.0, 17.5, 20.0, 18.0, 16.0);

    prices.put("normal", normalPrice);
    prices.put("holofoil", holoPrice);

    TcgPlayerDTO tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTOWithPrices(url, updatedAt, prices);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    TcgPlayerReporting result = mapper.toEntity(tcgPlayerDTO, card);

    assertThat(result).isNotNull();
    assertThat(result.getUrl()).isEqualTo(url);
    assertThat(result.getUpdatedAt()).isEqualTo(DateUtil.parseDate(updatedAt, Constantes.STAGING_CARD_DATE_FORMAT));
    assertThat(result.getCard()).isEqualTo(card);
  }

  @Test
  void toEntity_shouldHandleNullTcgPlayerDTO() {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    TcgPlayerReporting result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldHandleNullCard() {
    String url = "https://tcgplayer.com/card123";
    String updatedAt = "2023/05/15";
    TcgPlayerDTO tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTO(url, updatedAt);

    TcgPlayerReporting result = mapper.toEntity(tcgPlayerDTO, null);

    assertThat(result).isNull();
  }
}