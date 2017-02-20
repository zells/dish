package org.zells.dish.fakes;

import org.zells.dish.util.UuidGenerator;

public class FakeUuidGenerator implements UuidGenerator {

    private int uuid = 0;

    public String generate() {
        return "a-uuid-" + uuid++;
    }
}
