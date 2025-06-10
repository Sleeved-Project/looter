package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;

@ExtendWith(MockitoExtension.class)
public class HashImageMapperTest {

    @InjectMocks
    private HashImageMapper hashImageMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void toHashImageDTO_shouldReturnHashImageDTO_whenResponseContainsHash() {
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId("swsh12-1");
        cardImageDTO.setImageUrl("http://example.com/image.jpg");
        
        ObjectNode response = objectMapper.createObjectNode();
        response.put("hash", "abc123def456");
        
        HashImageDTO result = hashImageMapper.toHashImageDTO(cardImageDTO, response);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("swsh12-1");
        assertThat(result.getHash()).isEqualTo("abc123def456");
    }
    
    @Test
    void toHashImageDTO_shouldReturnNull_whenResponseIsNull() {
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId("swsh12-1");
        cardImageDTO.setImageUrl("http://example.com/image.jpg");
        
        HashImageDTO result = hashImageMapper.toHashImageDTO(cardImageDTO, null);
        
        assertThat(result).isNull();
    }
    
    @Test
    void toHashImageDTO_shouldThrowException_whenResponseDoesNotContainHash() {
        CardImageDTO cardImageDTO = new CardImageDTO();
        cardImageDTO.setCardId("swsh12-1");
        cardImageDTO.setImageUrl("http://example.com/image.jpg");
        
        ObjectNode response = objectMapper.createObjectNode();
        response.put("otherField", "someValue");
        
        assertThatThrownBy(() -> hashImageMapper.toHashImageDTO(cardImageDTO, response))
            .isInstanceOf(NullPointerException.class);
    }
}