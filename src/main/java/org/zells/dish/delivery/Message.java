package org.zells.dish.delivery;

import java.util.Set;

public interface Message {

    String value();

    Set<String> keys();

    Message read(String key);
}
