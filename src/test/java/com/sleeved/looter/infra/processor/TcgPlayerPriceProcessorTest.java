package com.sleeved.looter.infra.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.infra.mapper.TcgPlayerPriceMapper;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerPriceMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;
import com.sleeved.looter.mock.infra.TcgPlayerPriceDTOMock;

@ExtendWith(MockitoExtension.class)
class TcgPlayerPriceProcessorTest {

  @Mock
  private TcgPlayerPriceMapper tcgPlayerPriceMapper;

  @InjectMocks
  private TcgPlayerPriceProcessor processor;

  private TcgPlayerPriceDTO normalPriceDTO;
  private TcgPlayerPriceDTO holoPriceDTO;
  private TcgPlayerPrice normalPrice;
  private TcgPlayerPrice holoPrice;
  private TcgPlayerReporting tcgPlayerReporting;
  private Card card;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(1L,
        "https://tcgplayer.com/pikachu", LocalDate.now(), card);

    normalPriceDTO = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(1.0, 2.0, 3.0, 2.5, 1.5);
    holoPriceDTO = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(3.0, 4.0, 5.0, 4.5, 3.5);

    normalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(1L, "normal", 1.0, 2.0, 3.0, 2.5, 1.5,
        tcgPlayerReporting);
    holoPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(2L, "holofoil", 3.0, 4.0, 5.0, 4.5, 3.5,
        tcgPlayerReporting);
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenDTOMapsIsNull() {
    Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO = null;

    List<TcgPlayerPrice> result = processor.processFromDTOs(tcgPlayerPricesDTO, tcgPlayerReporting);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(tcgPlayerPriceMapper, never()).toEntity(any(), anyString(), any());
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenDTOMapsIsEmpty() {
    Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO = Collections.emptyMap();

    List<TcgPlayerPrice> result = processor.processFromDTOs(tcgPlayerPricesDTO, tcgPlayerReporting);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(tcgPlayerPriceMapper, never()).toEntity(any(), anyString(), any());
  }

  @Test
  void processFromDTOs_shouldProcessSinglePriceType_whenMapContainsOneEntry() {
    Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO = new HashMap<>();
    tcgPlayerPricesDTO.put("normal", normalPriceDTO);

    when(tcgPlayerPriceMapper.toEntity(normalPriceDTO, "normal", tcgPlayerReporting)).thenReturn(normalPrice);

    List<TcgPlayerPrice> result = processor.processFromDTOs(tcgPlayerPricesDTO, tcgPlayerReporting);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(normalPrice);
    assertThat(result.get(0).getType()).isEqualTo("normal");
    assertThat(result.get(0).getTcgPlayerReporting()).isEqualTo(tcgPlayerReporting);

    verify(tcgPlayerPriceMapper).toEntity(normalPriceDTO, "normal", tcgPlayerReporting);
  }

  @Test
  void processFromDTOs_shouldProcessMultiplePriceTypes_whenMapContainsMultipleEntries() {
    Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO = new HashMap<>();
    tcgPlayerPricesDTO.put("normal", normalPriceDTO);
    tcgPlayerPricesDTO.put("holofoil", holoPriceDTO);

    when(tcgPlayerPriceMapper.toEntity(normalPriceDTO, "normal", tcgPlayerReporting)).thenReturn(normalPrice);
    when(tcgPlayerPriceMapper.toEntity(holoPriceDTO, "holofoil", tcgPlayerReporting)).thenReturn(holoPrice);

    List<TcgPlayerPrice> result = processor.processFromDTOs(tcgPlayerPricesDTO, tcgPlayerReporting);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    assertThat(result).containsExactlyInAnyOrder(normalPrice, holoPrice);

    TcgPlayerPrice normalPriceResult = result.stream().filter(p -> "normal".equals(p.getType())).findFirst().get();
    TcgPlayerPrice holoPriceResult = result.stream().filter(p -> "holofoil".equals(p.getType())).findFirst().get();

    assertThat(normalPriceResult.getLow()).isEqualTo(1.0);
    assertThat(normalPriceResult.getMid()).isEqualTo(2.0);
    assertThat(normalPriceResult.getHigh()).isEqualTo(3.0);
    assertThat(normalPriceResult.getMarket()).isEqualTo(2.5);
    assertThat(normalPriceResult.getDirectLow()).isEqualTo(1.5);

    assertThat(holoPriceResult.getLow()).isEqualTo(3.0);
    assertThat(holoPriceResult.getMid()).isEqualTo(4.0);
    assertThat(holoPriceResult.getHigh()).isEqualTo(5.0);
    assertThat(holoPriceResult.getMarket()).isEqualTo(4.5);
    assertThat(holoPriceResult.getDirectLow()).isEqualTo(3.5);

    verify(tcgPlayerPriceMapper, times(2)).toEntity(any(TcgPlayerPriceDTO.class), anyString(),
        any(TcgPlayerReporting.class));
  }

  @Test
  void processFromDTOs_shouldSkipNullEntries_whenMapperReturnsNull() {
    Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO = new HashMap<>();
    tcgPlayerPricesDTO.put("normal", normalPriceDTO);
    tcgPlayerPricesDTO.put("holofoil", holoPriceDTO);

    when(tcgPlayerPriceMapper.toEntity(normalPriceDTO, "normal", tcgPlayerReporting)).thenReturn(normalPrice);
    when(tcgPlayerPriceMapper.toEntity(holoPriceDTO, "holofoil", tcgPlayerReporting)).thenReturn(null);

    List<TcgPlayerPrice> result = processor.processFromDTOs(tcgPlayerPricesDTO, tcgPlayerReporting);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly(normalPrice);

    verify(tcgPlayerPriceMapper, times(2)).toEntity(any(TcgPlayerPriceDTO.class), anyString(),
        any(TcgPlayerReporting.class));
  }
}