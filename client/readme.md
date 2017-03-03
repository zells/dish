# zells client

Simple command line application for sending and receiving Messages, as well as connecting to the zells network.

## Client Mode

The following command builds and runs the client (version 0.1).

    ./gradlew buildClient
    java -jar build/zells-client-0.1.jar

## Server Mode

If the port is as command line argument, the application starts listening for Peer connections on that port

    java -jar build/zells-client-0.1.jar 42420

## Send Message

Every line entered sends a message. The syntax is

    receiver [message]

If the `message` is omitted, `null` is sent.

The `message` can be written in *JSON* or in *short syntax*.

    message := "!" JsonString | ShortSyntax

Strings starting with `0x` are interpreted as hexadecimal binary numbers.

The following shows examples in short syntax and their equivalents in JSON.

    foo                 ["foo"]
    42                  [42]
    0x42                ["0x42"] // The binary number 01000010
    yes                 [true]
    no                  [false]
    foo bar             ["foo", "bar"]
    foo:bar             {"foo":"bar"}
    foo bar:yes         {0:"foo", "bar":true}
    bar:yes foo         {"bar":true, 0:"foo"}
    foo:bar foo:baz     {"foo":["bar", "baz"]}
    "foo bar"           ["foo bar"]
    foo\:\ bar          ["foo: bar"]

## Usage

Everything is done by sending messages to Zells. A message can be sent to the client itself using the alias `client`. The following message just echoes what it receives.

    < client say:"Hello World"
    Hello World

### Join Peer

Connects the Dish of the client to another Dish. The example uses the default values of `host` and `port`.

    < client join host:localhost port:42420
    Joined tcp:localhost:42420

### Use Aliases

The Address of the receiver Zell can either be entered as a hexadecimal number or using an alias. The example lists the currently registered aliases, adds "foo" as a new alias for `0xfaba`, and then lists all aliases again.

    < client alias
    Aliases:
    client: 0xda4d3947c0d249929230c77ffa9cbb71
    < client alias use:foo for:0xfaba
    Set alias [foo] for [0xfaba]
    < client alias
    Aliases:
    foo: 0xfaba
    client: 0xda4d3947c0d249929230c77ffa9cbb71

An alias can be used in a message by prefixing it with an `@`.

    < client say:@foo
    0xfaba

### Receive Message

Messages can be best received by creating a *listener zell*. The following example creates a new listener under the alias "me".

    < client listen as:me
    Listening on 0xfd7557ba57904f6aa61c34c873a1731a
    Set alias [me] for [0xfd7557ba57904f6aa61c34c873a1731a]
    < me Hello World
    0> {0:Hello, 1:World}

### Reference Message

Received messages and parts of them can be referenced by using its ID prefixed by `#`.

    < client say:#0
    {0:Hello, 1:World}
    < client say:#0.1
    World

### Exit

The client is exited with

    < client exit
    Good-bye


## Example

The following example starts two clients, connects one to the other and then creates listener zells on each to have a conversation. The `---------` lines indicate a switch between two terminal.

    $ java -jar build/zells-client-0.1.jar 42420
    Started server on port 42420
    < client listen as:me
    Listening on 0xfa0624a8d4b24331bf691684338d1b3f
    Set alias [me] for [0xfa0624a8d4b24331bf691684338d1b3f]
    <
    ---------
    $ java -jar build/zells-client-0.1.jar
    < client join
    Joined tcp:localhost:42420
    < client alias use:other for:0xfa0624a8d4b24331bf691684338d1b3f
    Set alias [other] for [0xfa0624a8d4b24331bf691684338d1b3f]
    < client listen as:me
    Listening on 0x902de2010eeb4b2d8c6968cb0643a3ea
    Set alias [me] for [0x902de2010eeb4b2d8c6968cb0643a3ea]
    < other "Hello World" from:@me
    <
    ---------
    0> {0:Hello World, from:0x902de2010eeb4b2d8c6968cb0643a3ea}
    < #0.from "Hello Back" from:@me
    <
    ---------
    0> {0:Hello Back, from:0xfa0624a8d4b24331bf691684338d1b3f}
    <
