package com.sleeved.looter.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.domain.repository.staging.StagingPriceRepository;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.StagingPriceMock;

@ExtendWith(MockitoExtension.class)
class StagingCardToCardPriceDTOReaderTest {

  @Mock
  private StagingPriceRepository stagingPriceRepository;

  @Mock
  private LooterScrapingErrorHandler errorHandler;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private StagingCardToCardPriceDTOReader reader;

  @BeforeEach
  void setUp() {
    when(stagingPriceRepository.findAll()).thenReturn(List.of());
  }

  @Test
  void read_shouldReturnCardPriceDTOWhenStagingPricesExist() throws Exception {
    int priceCount = 2;
    List<StagingPrice> mockPrices = StagingPriceMock.createMockStagingPricesList(priceCount, 1L);

    StagingPrice firstPrice = mockPrices.get(0);
    String validJson = "{\"id\":\"price0\",\"name\":\"Test Price 0\",\"tcgplayer\":{\"url\":\"https://tcgplayer.com/price0\",\"updatedAt\":\"2023/05/15\",\"prices\":{\"normal\":{\"low\":1.99,\"mid\":2.99,\"high\":3.99,\"market\":2.49}}},\"cardmarket\":{\"url\":\"https://cardmarket.com/price0\",\"updatedAt\":\"2023/05/15\"}}";
    firstPrice.setPayload(validJson);

    StagingPrice secondPrice = mockPrices.get(1);
    String validJson2 = "{\"id\":\"price1\",\"name\":\"Test Price 1\",\"tcgplayer\":{\"url\":\"https://tcgplayer.com/price1\",\"updatedAt\":\"2023/05/15\",\"prices\":{\"holofoil\":{\"low\":5.99,\"mid\":7.99,\"high\":9.99,\"market\":6.49}}},\"cardmarket\":{\"url\":\"https://cardmarket.com/price1\",\"updatedAt\":\"2023/05/15\"}}";
    secondPrice.setPayload(validJson2);

    when(stagingPriceRepository.findAll()).thenReturn(mockPrices);

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    CardPriceDTO firstCardPrice = reader.read();
    CardPriceDTO secondCardPrice = reader.read();
    CardPriceDTO thirdCardPrice = reader.read();

    assertThat(firstCardPrice).isNotNull();
    assertThat(firstCardPrice.getId()).isEqualTo("price0");
    assertThat(firstCardPrice.getName()).isEqualTo("Test Price 0");
    assertThat(firstCardPrice.getTcgplayer()).isNotNull();
    assertThat(firstCardPrice.getTcgplayer().getUrl()).isEqualTo("https://tcgplayer.com/price0");
    assertThat(firstCardPrice.getCardmarket()).isNotNull();
    assertThat(firstCardPrice.getCardmarket().getUrl()).isEqualTo("https://cardmarket.com/price0");

    assertThat(secondCardPrice).isNotNull();
    assertThat(secondCardPrice.getId()).isEqualTo("price1");
    assertThat(secondCardPrice.getName()).isEqualTo("Test Price 1");
    assertThat(secondCardPrice.getTcgplayer()).isNotNull();
    assertThat(secondCardPrice.getTcgplayer().getUrl()).isEqualTo("https://tcgplayer.com/price1");
    assertThat(secondCardPrice.getCardmarket()).isNotNull();
    assertThat(secondCardPrice.getCardmarket().getUrl()).isEqualTo("https://cardmarket.com/price1");

    assertThat(thirdCardPrice).isNull();
  }

  @Test
  void read_shouldReturnNullWhenNoMoreStagingPrices() throws Exception {
    List<StagingPrice> emptyPrices = StagingPriceMock.createMockStagingPricesList(0, 1L);
    when(stagingPriceRepository.findAll()).thenReturn(emptyPrices);

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    CardPriceDTO result = reader.read();

    assertThat(result).isNull();
  }

  @Test
  void read_shouldProcessAllPricesInOrder() throws Exception {
    int priceCount = 3;
    List<StagingPrice> mockPrices = StagingPriceMock.createMockStagingPricesList(priceCount, 1L);

    for (int i = 0; i < priceCount; i++) {
      StagingPrice price = mockPrices.get(i);
      String validJson = "{\"id\":\"price" + i + "\",\"name\":\"Test Price " + i
          + "\",\"tcgplayer\":{\"url\":\"https://tcgplayer.com/price" + i
          + "\",\"updatedAt\":\"2023/05/15\"},\"cardmarket\":{\"url\":\"https://cardmarket.com/price" + i
          + "\",\"updatedAt\":\"2023/05/15\"}}";
      price.setPayload(validJson);
    }

    when(stagingPriceRepository.findAll()).thenReturn(mockPrices);

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    for (int i = 0; i < priceCount; i++) {
      CardPriceDTO cardPrice = reader.read();
      assertThat(cardPrice).isNotNull();
      assertThat(cardPrice.getId()).isEqualTo("price" + i);
      assertThat(cardPrice.getName()).isEqualTo("Test Price " + i);
      assertThat(cardPrice.getTcgplayer()).isNotNull();
      assertThat(cardPrice.getTcgplayer().getUrl()).isEqualTo("https://tcgplayer.com/price" + i);
      assertThat(cardPrice.getCardmarket()).isNotNull();
      assertThat(cardPrice.getCardmarket().getUrl()).isEqualTo("https://cardmarket.com/price" + i);
    }

    CardPriceDTO noMorePrice = reader.read();
    assertThat(noMorePrice).isNull();
  }

  @Test
  void read_shouldHandleErrorWhenPayloadIsInvalidJson() throws Exception {
    StagingPrice invalidPrice = new StagingPrice();
    invalidPrice.setId("invalid-id");
    invalidPrice.setPayload("{invalid-json}");

    when(stagingPriceRepository.findAll()).thenReturn(List.of(invalidPrice));

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    CardPriceDTO result = reader.read();

    assertThat(result).isNull();
    verify(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
  }

  @Test
  void read_shouldThrowExceptionWhenErrorHandlerThrows() throws Exception {
    StagingPrice invalidPrice = new StagingPrice();
    invalidPrice.setId("invalid-id");
    invalidPrice.setPayload("{invalid-json}");

    when(stagingPriceRepository.findAll()).thenReturn(List.of(invalidPrice));

    LooterScrapingException expectedException = new LooterScrapingException("Erreur de traitement", new Exception());
    doThrow(expectedException).when(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    assertThatThrownBy(() -> reader.read())
        .isInstanceOf(LooterScrapingException.class);

    verify(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
  }

  @Test
  void read_shouldUseCorrectConstantesForErrorHandling() throws Exception {
    StagingPrice invalidPrice = new StagingPrice();
    invalidPrice.setId("invalid-id");
    invalidPrice.setPayload("{invalid-json}");

    when(stagingPriceRepository.findAll()).thenReturn(List.of(invalidPrice));

    reader = new StagingCardToCardPriceDTOReader(stagingPriceRepository, objectMapper, errorHandler);

    reader.read();

    verify(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
  }
}