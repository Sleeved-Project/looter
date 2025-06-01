package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.infra.dto.CardMarketDTO;
import com.sleeved.looter.infra.dto.CardMarketPriceDTO;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.infra.CardMarketDTOMock;
import com.sleeved.looter.mock.infra.CardMarketPriceDTOMock;

@ExtendWith(MockitoExtension.class)
public class CardMarketPriceMapperTest {

  @InjectMocks
  private CardMarketPriceMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    CardMarketDTO cardMarketDTO = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/card123",
        "2023/05/15");

    CardMarketPriceDTO cardMarketPriceDTO = CardMarketPriceDTOMock.createMockCardMarketPriceDTO(
        10.50, 10.25, 9.75, 9.00, 8.50,
        7.50, 7.00, 11.20, 10.80, 10.25,
        9.75, 9.50, 9.25, 11.00, 10.00);

    cardMarketDTO.setPrices(cardMarketPriceDTO);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice result = mapper.toEntity(cardMarketDTO, card);

    assertThat(result).isNotNull();
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(LocalDate.of(2023, 5, 15));
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAverageSellPrice()).isEqualTo(10.50);
    assertThat(result.getAvg1()).isEqualTo(10.25);
    assertThat(result.getAvg7()).isEqualTo(9.75);
    assertThat(result.getAvg30()).isEqualTo(9.00);
    assertThat(result.getGermanProLow()).isEqualTo(8.50);
    assertThat(result.getLowPrice()).isEqualTo(7.50);
    assertThat(result.getLowPriceExPlus()).isEqualTo(7.00);
    assertThat(result.getReverseHoloAvg1()).isEqualTo(11.20);
    assertThat(result.getReverseHoloAvg7()).isEqualTo(10.80);
    assertThat(result.getReverseHoloAvg30()).isEqualTo(10.25);
    assertThat(result.getReverseHoloLow()).isEqualTo(9.75);
    assertThat(result.getReverseHoloSell()).isEqualTo(9.50);
    assertThat(result.getReverseHoloTrend()).isEqualTo(9.25);
    assertThat(result.getSuggestedPrice()).isEqualTo(11.00);
    assertThat(result.getTrendPrice()).isEqualTo(10.00);
  }

  @Test
  void toEntity_shouldHandleNullPrices() {
    CardMarketDTO cardMarketDTO = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/card123",
        "2023/05/15");

    cardMarketDTO.setPrices(null);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice result = mapper.toEntity(cardMarketDTO, card);

    assertThat(result).isNotNull();
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(LocalDate.of(2023, 5, 15));
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAverageSellPrice()).isNull();
    assertThat(result.getAvg1()).isNull();
    assertThat(result.getAvg7()).isNull();
    assertThat(result.getAvg30()).isNull();
    assertThat(result.getGermanProLow()).isNull();
    assertThat(result.getLowPrice()).isNull();
    assertThat(result.getLowPriceExPlus()).isNull();
    assertThat(result.getReverseHoloAvg1()).isNull();
    assertThat(result.getReverseHoloAvg7()).isNull();
    assertThat(result.getReverseHoloAvg30()).isNull();
    assertThat(result.getReverseHoloLow()).isNull();
    assertThat(result.getReverseHoloSell()).isNull();
    assertThat(result.getReverseHoloTrend()).isNull();
    assertThat(result.getSuggestedPrice()).isNull();
    assertThat(result.getTrendPrice()).isNull();
  }

  @Test
  void toEntity_shouldHandleNullCardMarketDTO() {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldHandleNullCard() {
    CardMarketDTO cardMarketDTO = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/card123",
        "2023/05/15");

    CardMarketPrice result = mapper.toEntity(cardMarketDTO, null);

    assertThat(result).isNull();
  }
}