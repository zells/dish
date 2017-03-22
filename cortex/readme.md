# zells cortex

A graphical user interface to send and receive messages.

*this document is work-in-progress*

## Zells

### Cortex

    cortex < say:HelloWorld to:@+
    cortex < join host:localhost port:42420
    cortex < listen:42420
    cortex < stop:42420
    cortex < exit

### Address Book

    book < tell to:@+
    book < use:foo for:@+

    cortex < say:@foo to:@+
    foo < Hi

    book < forget:foo

## Synapses

### Communicator

    . < say:"Hello World" to:@+

#### Syntax

A *message send* consists of a receiver and the message (defaults to `null`).

    messageSend := receiver ("<" message)?

The Address of the *receiver* can be entered as a hexadecimal number.

    receiver := ("0x")? [0-9a-f]+

A message can either be a *scalar* or a *composite* type.

    message := scalar | composite

The supported scalar types are

    scalar := null | boolean | integer | string | binary | address

The composite type maps string *keys* to message *values*.

    composite := (string message)*

The *message* can be written in *JSON* or in *short syntax*.

    message := ("!" Json) | ShortSyntax

The message types are expressed in JSON in the following way.

    null            null
    boolean         true|false
    integer         1234
    string          "foo"
    binary          "0x1234567890abcdef"
    address         "@0x1234567890abcdef"
    composite       {"foo":"bar"}|["foo", "bar"]

The following shows examples in short syntax and their equivalents in JSON.

    foo                 ["foo"]
    42                  [42]
    yes                 [true]
    no                  [false]
    0x42                ["0x42"] // The binary number 01000010
    @0x42               ["@0x42"] // The Adress 0x42
    @foo                ["@foo"] // The address of "foo"
    foo bar             ["foo", "bar"]
    foo:bar             {"foo":"bar"}
    foo bar:yes         {0:"foo", "bar":true}
    bar:yes foo         {"bar":true, 0:"foo"}
    foo:bar foo:baz     {"foo":["bar", "baz"]}
    "foo: bar"           ["foo: bar"]
    foo\:\ bar          ["foo: bar"]