package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.repository.iris.HashCardRepository;
import com.sleeved.looter.mock.domain.HashCardMock;

@ExtendWith(MockitoExtension.class)
class HashCardServiceTest {

    @Mock
    private HashCardRepository hashCardRepository;
    
    private HashCardService hashCardService;
    
    @BeforeEach
    void setUp() {
        hashCardService = new HashCardService(hashCardRepository);
    }
    
    @Test
    void getOrCreate_ShouldReturnExistingCard_WhenCardExists() {
        // Arrange
        HashCard existingCard = HashCardMock.createMock("test-card-1", "existing-hash-value");
        
        HashCard inputCard = HashCardMock.createMock("test-card-1", "existing-hash-value");
        
        when(hashCardRepository.findById("test-card-1")).thenReturn(Optional.of(existingCard));
        
        // Act
        HashCard result = hashCardService.getOrCreate(inputCard);
        
        // Assert
        assertThat(result).isEqualTo(existingCard);
        assertThat(result.getHash()).isEqualTo("existing-hash-value");
        verify(hashCardRepository).findById("test-card-1");
        verify(hashCardRepository, never()).save(any());
    }
    
    @Test
    void getOrCreate_ShouldSaveAndReturnNewCard_WhenCardDoesNotExist() {
        // Arrange
        HashCard newCard = HashCardMock.createMock("test-card-2", "new-hash-value");
        when(hashCardRepository.findById("test-card-2")).thenReturn(Optional.empty());
        when(hashCardRepository.save(newCard)).thenReturn(newCard);
        
        // Act
        HashCard result = hashCardService.getOrCreate(newCard);
        
        // Assert
        assertThat(result).isEqualTo(newCard);
        verify(hashCardRepository).findById("test-card-2");
        verify(hashCardRepository).save(newCard);
    }
    
    @Test
    void getOrCreate_ShouldPropagateException_WhenSaveFails() {
        // Arrange
        HashCard newCard = HashCardMock.createMock("test-card-3", "new-hash-value");
        
        RuntimeException dbException = new RuntimeException("Database error");
        
        when(hashCardRepository.findById("test-card-3")).thenReturn(Optional.empty());
        when(hashCardRepository.save(newCard)).thenThrow(dbException);
        
        // Act & Assert
        assertThatThrownBy(() -> hashCardService.getOrCreate(newCard))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
            
        verify(hashCardRepository).findById("test-card-3");
        verify(hashCardRepository).save(newCard);
    }
}