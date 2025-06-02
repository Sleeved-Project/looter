package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.repository.atlas.TcgPlayerReportingRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;

@ExtendWith(MockitoExtension.class)
class TcgPlayerReportingServiceTest {

    @Mock
    private TcgPlayerReportingRepository tcgPlayerReportingRepository;

    @InjectMocks
    private TcgPlayerReportingService tcgPlayerReportingService;

    @Test
    void getOrCreate_shouldReturnExistingReporting_whenReportingExists() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        TcgPlayerReporting existingReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card))
                .thenReturn(Optional.of(existingReporting));

        TcgPlayerReporting result = tcgPlayerReportingService.getOrCreate(inputReporting);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://tcgplayer.com/card123");
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(result.getCard()).isEqualTo(card);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card);
        verify(tcgPlayerReportingRepository, never()).save(any(TcgPlayerReporting.class));
    }

    @Test
    void getOrCreate_shouldCreateAndReturnNewReporting_whenReportingDoesNotExist() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        TcgPlayerReporting savedReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card))
                .thenReturn(Optional.empty());
        when(tcgPlayerReportingRepository.save(inputReporting)).thenReturn(savedReporting);

        TcgPlayerReporting result = tcgPlayerReportingService.getOrCreate(inputReporting);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://tcgplayer.com/card123");
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(result.getCard()).isEqualTo(card);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card);
        verify(tcgPlayerReportingRepository).save(inputReporting);
    }

    @Test
    void getOrCreate_shouldHandleDifferentDates() {
        LocalDate updatedAtInput = LocalDate.of(2023, 5, 15);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAtInput,
                card);

        TcgPlayerReporting savedReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAtInput,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAtInput, card))
                .thenReturn(Optional.empty());
        when(tcgPlayerReportingRepository.save(inputReporting)).thenReturn(savedReporting);

        TcgPlayerReporting result = tcgPlayerReportingService.getOrCreate(inputReporting);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://tcgplayer.com/card123");
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAtInput);
        assertThat(result.getCard()).isEqualTo(card);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAtInput, card);
        verify(tcgPlayerReportingRepository).save(inputReporting);
    }

    @Test
    void getOrCreate_shouldHandleDifferentCards() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card1 = CardMock.createBasicMockCard("card123", "Pikachu");
        Card card2 = CardMock.createBasicMockCard("card456", "Charizard");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card1);

        TcgPlayerReporting savedReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAt,
                card1);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card1))
                .thenReturn(Optional.empty());
        when(tcgPlayerReportingRepository.save(inputReporting)).thenReturn(savedReporting);

        TcgPlayerReporting result = tcgPlayerReportingService.getOrCreate(inputReporting);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://tcgplayer.com/card123");
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(result.getCard()).isEqualTo(card1);
        assertThat(result.getCard()).isNotEqualTo(card2);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card1);
        verify(tcgPlayerReportingRepository).save(inputReporting);
    }

    @Test
    void getByUpdatedAtAndCard_shouldReturnExistingReporting_whenReportingExists() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        TcgPlayerReporting existingReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card))
                .thenReturn(Optional.of(existingReporting));

        TcgPlayerReporting result = tcgPlayerReportingService.getByUpdatedAtAndCard(inputReporting);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("https://tcgplayer.com/card123");
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(result.getCard()).isEqualTo(card);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card);
    }

    @Test
    void getByUpdatedAtAndCard_shouldThrowException_whenReportingDoesNotExist() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting inputReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tcgPlayerReportingService.getByUpdatedAtAndCard(inputReporting))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("TcgPlayerReporting not found for updated at: " + updatedAt)
                .hasMessageContaining("card: " + card);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card);
    }

    @Test
    void getByUpdatedAtAndCard_shouldReturnCorrectReporting_forDifferentDates() {
        LocalDate firstDate = LocalDate.of(2023, 5, 15);
        LocalDate secondDate = LocalDate.of(2023, 6, 20);
        Card card = CardMock.createBasicMockCard("card123", "Pikachu");

        TcgPlayerReporting firstDateReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                firstDate,
                card);

        TcgPlayerReporting secondDateReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                secondDate,
                card);

        TcgPlayerReporting firstExistingReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                firstDate,
                card);

        TcgPlayerReporting secondExistingReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                2L,
                "https://tcgplayer.com/card123",
                secondDate,
                card);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(firstDate, card))
                .thenReturn(Optional.of(firstExistingReporting));
        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(secondDate, card))
                .thenReturn(Optional.of(secondExistingReporting));

        TcgPlayerReporting resultFirst = tcgPlayerReportingService.getByUpdatedAtAndCard(firstDateReporting);
        TcgPlayerReporting resultSecond = tcgPlayerReportingService.getByUpdatedAtAndCard(secondDateReporting);

        assertThat(resultFirst).isNotNull();
        assertThat(resultFirst.getId()).isEqualTo(1L);
        assertThat(resultFirst.getUpdatedAt()).isEqualTo(firstDate);

        assertThat(resultSecond).isNotNull();
        assertThat(resultSecond.getId()).isEqualTo(2L);
        assertThat(resultSecond.getUpdatedAt()).isEqualTo(secondDate);

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(firstDate, card);
        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(secondDate, card);
    }

    @Test
    void getByUpdatedAtAndCard_shouldReturnCorrectReporting_forDifferentCards() {
        LocalDate updatedAt = LocalDate.of(2023, 5, 15);
        Card card1 = CardMock.createBasicMockCard("card123", "Pikachu");
        Card card2 = CardMock.createBasicMockCard("card456", "Charizard");

        TcgPlayerReporting reportingForCard1 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card123",
                updatedAt,
                card1);

        TcgPlayerReporting reportingForCard2 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
                null,
                "https://tcgplayer.com/card456",
                updatedAt,
                card2);

        TcgPlayerReporting existingReportingForCard1 = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                1L,
                "https://tcgplayer.com/card123",
                updatedAt,
                card1);

        TcgPlayerReporting existingReportingForCard2 = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
                2L,
                "https://tcgplayer.com/card456",
                updatedAt,
                card2);

        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card1))
                .thenReturn(Optional.of(existingReportingForCard1));
        when(tcgPlayerReportingRepository.findByUpdatedAtAndCard(updatedAt, card2))
                .thenReturn(Optional.of(existingReportingForCard2));

        TcgPlayerReporting resultCard1 = tcgPlayerReportingService.getByUpdatedAtAndCard(reportingForCard1);
        TcgPlayerReporting resultCard2 = tcgPlayerReportingService.getByUpdatedAtAndCard(reportingForCard2);

        assertThat(resultCard1).isNotNull();
        assertThat(resultCard1.getId()).isEqualTo(1L);
        assertThat(resultCard1.getCard()).isEqualTo(card1);
        assertThat(resultCard1.getUrl()).isEqualTo("https://tcgplayer.com/card123");

        assertThat(resultCard2).isNotNull();
        assertThat(resultCard2.getId()).isEqualTo(2L);
        assertThat(resultCard2.getCard()).isEqualTo(card2);
        assertThat(resultCard2.getUrl()).isEqualTo("https://tcgplayer.com/card456");

        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card1);
        verify(tcgPlayerReportingRepository).findByUpdatedAtAndCard(updatedAt, card2);
    }
}