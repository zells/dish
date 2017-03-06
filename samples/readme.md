# zells sample applications

This module contains sample Zells and an application to run them on a Dish.

The sample Zells are:
- **[Lobby]** - a simple chat room

[Lobby]: https://github.com/zells/three/blob/master/samples/src/main/java/org/zells/samples/LobbyZell.java

## Server Mode

The following command builds and runs the current version of the *samples* Dish on port `42420`.

    ./gradlew buildSamples
    java -jar build/zells-samples-0.1.jar -s42420

You can now use a [Client] Dish to connect to it with and send messages to the Zells of the samples Dish, whose addresses are printed when the Dish is started. If the Address of the Lobby is `0x123fab1321` for example, you can use the following commands to join the samples Dish, register an alias for it the Lobby, and create a listener Zell for receiving messages.

    client < join host:localhost port:42420
    client < listen as:me
    client < alias use:lobby for:0x123fab1321

[Client]: https://github.com/zells/three/tree/master/client

## Join Dishes

To connect the *samples* Dish to other Dishes on the network, pass the connection descriptors with `-j` options. The following command connects the Dish to a Dish on `localhost` listening on port `42421`

    java -jar build/zells-samples-0.1.jar -jtcp:localhost:42421


## Lobby

The *Lobby* Zell is a simple chat room where you can find other people, talk to the room, to individuals and to people interested in a certain topic.

### Presence

People present in the Lobby are identified by their self-chosen name. When you enter a room, an *avatar* Zell is created which represents you identity. All actions in the room are executed by sending messages to that avatar. Here is an example execution

    << lobby < enter:@me as:John
    <<
    0> {0:Hello, John, avatar:0x299ff3bf5d6d41ef868025cddeeddccf}
    << client < alias use:avatar for:#0.avatar
    Set alias [avatar] for [0x299ff3bf5d6d41ef868025cddeeddccf]
    << avatar < leave
    1> Good-bye

### Talking

You can talk to everybody in the lobby but also to people individually. Here is an example conversation.

    << avatar < say:"Hello everybody"
    <<
    3> {from:Peter, message:Hello from me as well}
    << avatar < say:"How are you?" to:Peter
    <<
    4> {from:Peter, message:I'm good}
    <<

### Topics

You can also talk only to the people who are interested in a certain topic. To receive messages on a topic, you have to tell your avatar to join it.

    << avatar < join:dinos
    << avatar < say:"They're cool" on:dinos
    <<
    6> {from:Peter, message:I agree, on:dinos}
    <<

### Mailbox

If the Zell connected to your avatar becomes unavailable, the avatar saves all messages and sends them to you when you re-connect.

    << avatar < connect:@me
    <<
    0> {from:Peter, message:Which is your favourite, on:dinos}
    1> {from:Peter, message:Mine is the stegosaurus, on:dinos}
    2> {from:Peter, message:Join us on dinos}