# zells sample applications

This module contains sample Zells and an application to run them on a Dish.

The sample Zells are:
- [Lobby]

[Lobby]: https://github.com/zells/three/blob/master/samples/src/main/java/org/zells/samples/LobbyZell.java

## Server Mode

The following command builds and runs the current version of the *samples* Dish on port `42420`.

    ./gradlew buildSamples
    java -jar build/zells-samples-0.1.jar -s42420

You can now use a [Client] Dish to connect to it with and send messages to the Zells of the samples Dish, whose addresses are printed when the Dish is started. If the Address of the Lobby is `0x123fab1321` for example, use the following commands to join the samples Dish, create a listener Zell and register an alias for it the Lobby.

    client < join host:localhost port:42420
    client < listen as:me
    client < alias use:lobby for:0x123fab1321

[Client]: https://github.com/zells/three/tree/master/client

## Join Dishes

To connect the Dish to other Dishes on the network, pass the connection descriptors with `-j` options. The following command connects the Dish to a Dish listening on port `42421`

    java -jar build/zells-samples-0.1.jar -jtcp:localhost:42421


## Lobby

The *Lobby* Zell is a simple chat room where you can find others, talk to all, to individuals and to people interested in a topic. The following list gives an overview of the understood messages.

    hello:@me                           Lists all people in the lobby
    enter:@me as:John                   Enters the lobby as "John"
    leave:@me                           Leaves the lobby
    say:Hello as:@me                    Says "Hello" to everybody in the Lobby
    say:Hello to:Peter as:@me           Says "Hello" only to Peter
    inform:@me about:dinos              Start listening to all that is said on the topic "dinos"
    spare:@me about:dinos               Stop listening to things said about "dinos"
    say:Cool regarding:dinos as:@me     Says "Cool" to everybody interested in dinos
