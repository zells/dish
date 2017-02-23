package org.zells.dish.util;

import java.util.UUID;

public class BasicUuidGenerator implements UuidGenerator {

    public Uuid generate() {
        return Uuid.fromString(UUID.randomUUID().toString());
    }
}
