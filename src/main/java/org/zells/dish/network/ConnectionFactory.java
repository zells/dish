package org.zells.dish.network;

public interface ConnectionFactory {

    boolean canBuild(String description);

    Connection build(String description);
}
