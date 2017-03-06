# zells (prototype three) [![Build Status](https://travis-ci.org/zells/three.svg?branch=master)](https://travis-ci.org/zells/three)

This is a prototype of the *zells* system used to explore the design of the *zells messaging protocol* and the implementation of a distributed peer-to-peer network based on it.


## Concepts

A *Zell* encapsulates behaviour. Each zell has a unique *Address* which can be used to send it asynchronous *Messages* with arbitrary but structured content. Each Message is then exactly once if the receiver can be found.

Zells live on a *Dish*. A Dish can be connected to other Dish, forming a *Network* of Zells. Messages can be sent to any Zell on the Network using its Address, regardless of the physical location of the Zell's Dish.


## Installation

The project can be downloaded with [git] and built with [gradle].

    git clone https://github.com/zells/three.git zells
    cd zells
    ./gradlew check

[git]: https://git-scm.com
[gradle]: https://gradle.org/


## Usage

The usage of each module is described in the module. They are

- **[dish]** - Library for sending and receiving Messages on a Zells Network
- **[client]** - Minimal user interface to the Dish

[dish]: https://github.com/zells/interface/tree/master/dish
[client]: https://github.com/zells/interface/tree/master/client


## Documentation ##

This project is a work-in-progress prototype with minimal documentation. If you have any question or comment, please don't hesitate to [contact me].

[contact me]: http://rtens.org/#contact


## Contribution ##

Any kind of contribution will be much appreciated. Not just code but also comments and general remarks. Just [drop me a line][contact me] or open a [new issue].

[new issue]: https://github.com/zells/qi/issues/new


## License

The documents and software in this repository are licensed under the [GPLv3] License.

[GPLv3]: http://www.gnu.org/licenses/gpl-3.0.html
