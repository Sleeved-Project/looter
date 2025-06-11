package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.iris.HashCard;

public class HashCardMock {

    public static final String ID = "test-card-1";
    public static final String HASH = "test-hash-value";

    public static HashCard createMock() {
        HashCard card = new HashCard();
        card.setId(ID);
        card.setHash(HASH);
        return card;
    }

    public static HashCard createMock(String id, String hash) {
        HashCard card = new HashCard();
        card.setId(id);
        card.setHash(hash);
        return card;
    }

}
