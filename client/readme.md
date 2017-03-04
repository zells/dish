# zells client

Simple command line application for sending and receiving Messages, as well as connecting to other Dishes.

## Client Mode

The following command builds and runs the current version of the client.

    ./gradlew buildClient
    java -jar build/zells-client-0.1.jar

## Server Mode

If a port is provided as a command line argument, the application starts listening for connections from other Dishes on that port.

    java -jar build/zells-client-0.1.jar 42420

## Send Message

Every line entered sends a message. The following input sends the message "Hello Zell" to the Zell at the Address `1234`.

    0x1234 < "Hello World"

The Address is a hexadecimal number (`0001001000110100` in binary), marked by the prefix `0x`. If the message is omitted, `null` is sent.

## Usage

Everything is done by sending messages to Zells. A message can be sent to the client itself using the alias `client`. The following message just echoes what it receives.

    client < say:"Hello World"

### Join Network

To join the zells Network, the client's Dish needs to be connected to another Dish. The following example uses the default values of `host` and `port`.

    client < join host:localhost port:42420

### Use Aliases

Because Addresses are very long hexadecimal numbers, aliases can be registered for specific Addresses and used in their places. The following message lists all registered aliases.

    client < alias list

This messages registers the alias "foo" for the Address `fadaba`.

    client < alias use:foo for:0xfadaba

An alias can be used in a message by prefixing it with an `@`.

    client < say:@foo

### Receive Message

Received messages are printed by a *listener zell*. The following message creates a new listener under the alias "me".

    client < listen as:me

Use the alias to add the Address of the listener zell to your messages.

    me < message:"Hello World" from:@me

Messages received by a listener Zell are printed with a sequential number like this.

    0> {message:Hello World from:0xfaba13287a}

### Reference Message

Any part of a received message can be referenced by using its number prefixed by `#` and its part separated by a dot.

    #0.from < echo:#0.message from:@me

### Exit

The client is exited with

    client < exit

## Syntax

The syntax of a message send is

    messageSend := receiver ("<" message)?

The `receiver` Address can be entered as a hexadecimal number.

    receiver := ("0x")? [0-9a-f]+

A message can either be a scalar or a composite type.

    message := scalar | composite

The supported scalar types are

    scalar := null | boolean | integer | string | binary

The composite type maps strings to messages

    composite := (key message)*

The `message` can be written in *JSON* or in *short syntax*.

    message := ("!" Json) | ShortSyntax

JSON maps to the messages in the following way

    null            null
    boolean         true|false
    integer         1234
    string          "foo"
    binary          "0x1234567890abcdef"
    composite       {"foo":"bar"}|["foo", "bar"]

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


## Example

The following example starts two clients, connects one to the other and then creates listener zells on each to have a conversation. The `---------` lines indicate a switch between two terminals.

```diff
    $ java -jar build/zells-client-0.1.jar 42420
    + Started server on port 42420
    << client < listen as:me
    + Listening on 0xfa0624a8d4b24331bf691684338d1b3f
    + Set alias [me] for [0xfa0624a8d4b24331bf691684338d1b3f]
    <<
    ---------
    $ java -jar build/zells-client-0.1.jar
    << client < join
    + Joined tcp:localhost:42420
    << client < alias use:other for:0xfa0624a8d4b24331bf691684338d1b3f
    + Set alias [other] for [0xfa0624a8d4b24331bf691684338d1b3f]
    << client < listen as:me
    + Listening on 0x902de2010eeb4b2d8c6968cb0643a3ea
    + Set alias [me] for [0x902de2010eeb4b2d8c6968cb0643a3ea]
    << other < "Hello World" from:@me
    <<
    ---------
    + 0> {0:Hello World, from:0x902de2010eeb4b2d8c6968cb0643a3ea}
    << #0.from < "Hello Back" from:@me
    <<
    ---------
    + 0> {0:Hello Back, from:0xfa0624a8d4b24331bf691684338d1b3f}
    <<