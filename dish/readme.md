# zells dish

A library for connecting to Peers, sending messages, and creating zells that react to received messages.

## Build

The following commands builds a `.jar` file in `build` under the root folder, that can be included in your project.

    ./gradlew buildDish

## Usage

The easiest way to create a new [`Dish`] instance is

    Dish myDish = Dish.buildDefault();

### Receive Message

Messages can be received by adding a new [`Zell`] to the Dish.

    myDish.add(new MyZell());

The `receive(Message message)` method is called every time the Zell receives a `Message`.

    class MyZell implements Zell {
        public void receive(Message message) {
            // Message is {"foo":{42:"Hello World"}}
            System.log(message.read("foo").read(42).asString());
        }
    }

The content of a Message is dynamic and can therefore be accessed as different types with the methods `isNull()`,`asString()`, `isTrue()`, `asInteger()`, `asBytes()`, as `asAddress()`. Fields of a Message can be read with `read(String key)` or `read(int key)` and return a `NullMessage` if the key does not exist. All keys are returned by `keys()`.

### Join Peer

To join a Peer Dish, a [`Connection`] needs to be created. This can be easily done with a `ConnectionRepository`.

    ConnectionRepository connections = new ConnectionRepository()
            .addAll(ConnectionRepository.supportedConnections());
    Connection connection = connections.getConnectionOf("tcp:localhost:42420");
    dish.join(connection);

## Example

You can find an example in the [`Client`].

[`Dish`]: https://github.com/zells/three/blob/master/dish/src/main/java/org/zells/dish/Dish.java
[`Zell`]: https://github.com/zells/three/blob/master/dish/src/main/java/org/zells/dish/Zell.java
[`Connection`]: https://github.com/zells/three/blob/master/dish/src/main/java/org/zells/dish/network/connecting/Connection.java
[`Client`]: https://github.com/zells/three/blob/master/client/src/main/java/org/zells/client/Client.java
