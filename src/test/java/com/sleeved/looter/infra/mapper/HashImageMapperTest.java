package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.mock.infra.CardImageDTOMock;

@ExtendWith(MockitoExtension.class)
public class HashImageMapperTest {

    @InjectMocks
    private HashImageMapper hashImageMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String IMAGE_URL = "http://example.com/image.jpg";

    @Test
    void toHashCard_shouldReturnHashCard_whenResponseContainsHash() {
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(IMAGE_URL);
        
        ObjectNode response = objectMapper.createObjectNode();
        response.put("hash", "abc123def456");
        
        HashCard result = hashImageMapper.toHashCard(cardImageDTO, response);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("card-image-id");
        assertThat(result.getHash()).isEqualTo("abc123def456");
    }
    
    @Test
    void toHashCard_shouldReturnNull_whenResponseIsNull() {
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(IMAGE_URL);
        
        HashCard result = hashImageMapper.toHashCard(cardImageDTO, null);
        
        assertThat(result).isNull();
    }
    
    @Test
    void toHashCard_shouldThrowException_whenResponseDoesNotContainHash() {
        CardImageDTO cardImageDTO = CardImageDTOMock.createCardImageDTO(IMAGE_URL);
        
        ObjectNode response = objectMapper.createObjectNode();
        response.put("otherField", "someValue");
        
        assertThatThrownBy(() -> hashImageMapper.toHashCard(cardImageDTO, response))
            .isInstanceOf(NullPointerException.class);
    }
}