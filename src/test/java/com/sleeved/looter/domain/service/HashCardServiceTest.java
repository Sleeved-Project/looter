package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
    private HashCardRepository repo;

    private HashCardService service;

    @BeforeEach
    void setUp() {
        service = new HashCardService(repo);
    }

    @Test
    void saveAll_ShouldDelegateToRepository() {
        HashCard c1 = HashCardMock.createMock("c1", "h1");
        HashCard c2 = HashCardMock.createMock("c2", "h2");
        List<HashCard> cards = List.of(c1, c2);

        when(repo.saveAll(cards)).thenReturn(cards);

        List<HashCard> result = service.saveAll(cards);

        assertThat(result).containsExactly(c1, c2);
        verify(repo).saveAll(cards);
    }

    @Test
    void save_ShouldDelegateToRepository() {
        HashCard c1 = HashCardMock.createMock("c1", "h1");
        when(repo.save(c1)).thenReturn(c1);

        HashCard result = service.save(c1);

        assertThat(result).isEqualTo(c1);
        verify(repo).save(c1);
    }
}
