# zells (prototype three) [![Build Status](https://travis-ci.org/zells/three.svg?branch=master)](https://travis-ci.org/zells/three)

This is a prototype of the *zells* system used to explore the design of the *zells messaging protocol* and the implementation of a distributed peer-to-peer network based on it.


## Concepts

A *Zell* encapsulates behaviour. Each zell has a unique *Address* which can be used to send it asynchronous *Messages* of arbitrary but structured content. Each Message is delivered exactly once if the receiver can be found.

Zells live on a *Dish*. A Dish can be connected to another Dish, forming a Network of Zells. Messages can be sent to any Zell on a Network using its Address, regardless of the physical location of the Zell.


## Installation

The project is built with [gradle].

    git clone https://github.com/zells/interface.git
    cd interface
    ./gradlew check

[gradle]: https://gradle.org/


## Usage

This project is a library for building distributed applications. The following command will pack all dependencies of the `Dish` module into `build/zells-dish-0.1.jar`.

    ./gradlew buildDish

Check out the [samples] to see how the library is used.

There is also a [client] for sending and receiving messages.

[samples]: https://github.com/zells/interface/tree/master/samples
[client]: https://github.com/zells/interface/tree/master/client


## Documentation ##

This project is a work-in-progress prototype with minimal documentation. If you have any question or comment, please don't hesitate to [contact me].

[contact me]: https://github.com/rtens


## Contribution ##

Any kind of contribution will be much appreciated. Not just code but also comments and general remarks. Just [drop me a line][contact me] or open a [new issue].

[new issue]: https://github.com/zells/qi/issues/new


## License

The documents and software in this repository are licensed under the [GPLv3] License.

[GPLv3]: http://www.gnu.org/licenses/gpl-3.0.html