package org.zells.dish.tests.fakes;

import org.zells.dish.util.Uuid;
import org.zells.dish.util.UuidGenerator;

public class FakeUuidGenerator implements UuidGenerator {

    private int uuid = 0;

    public Uuid generate() {
        return Uuid.fromString("abc" + uuid++);
    }
}
