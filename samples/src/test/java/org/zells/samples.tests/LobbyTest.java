package org.zells.samples.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.BinaryMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;
import org.zells.samples.LobbyZell;
import org.zells.samples.tests.fakes.FakeZell;

public class LobbyTest {

    private Dish dish;
    private Address lobby;
    private FakeZell foo;
    private FakeZell bar;
    private FakeZell baz;

    @Before
    public void setUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository()) {
            @Override
            public Messenger send(Address receiver, Message message) {
                return super.send(receiver, message).sync();
            }
        };
        lobby = dish.add(new LobbyZell(dish));
        foo = addZell(dish);
        bar = addZell(dish);
        baz = addZell(dish);
    }

    private FakeZell addZell(Dish dish) {
        FakeZell zell = new FakeZell();
        zell.address = dish.add(zell);
        return zell;
    }

    @Test
    public void enter() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));

        assert foo.received.get(0).read(0).equals(new StringMessage("Hello, foo"));
        assert foo.received.get(0).read("avatar") instanceof BinaryMessage;
    }

    @Test
    public void introducePeople() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        lobby(new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", baz.addressMessage()));

        assert baz.received(new CompositeMessage()
                .put("people", new CompositeMessage()
                        .put(0, new StringMessage("bar"))
                        .put(1, new StringMessage("foo"))));
    }

    @Test
    public void alterEgo() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("bar")));
        lobby(new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", baz.addressMessage()));

        assert baz.received(new CompositeMessage()
                .put("people", new CompositeMessage()
                        .put(0, new StringMessage("bar"))
                        .put(1, new StringMessage("foo"))));
    }

    @Test
    public void nameTaken() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("foo")));

        assert bar.received(new CompositeMessage()
                .put("error", new StringMessage("There is already somebody here with that name.")));
    }

    @Test
    public void leave() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        avatar(foo, new CompositeMessage()
                .put(0, new StringMessage("leave")));

        assert foo.received(new StringMessage("Good-bye"));

        lobby(new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", bar.addressMessage()));

        assert bar.received(new CompositeMessage()
                .put("people", new CompositeMessage()));
    }

    @Test
    public void talkToAll() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        lobby(new CompositeMessage()
                .put("enter", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        avatar(foo, new CompositeMessage()
                .put("say", new StringMessage("Hello World")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("from", new StringMessage("foo"));

        assert bar.received(message);
        assert baz.received(message);
        assert !foo.received(message);
    }

    @Test
    public void leftLobby() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        avatar(bar, new CompositeMessage()
                .put(0, new StringMessage("leave")));
        avatar(foo, new CompositeMessage()
                .put("say", new StringMessage("Hello World")));

        assert bar.received.size() == 2;
    }

    @Test
    public void talkToSomebody() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        lobby(new CompositeMessage()
                .put("enter", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        avatar(foo, new CompositeMessage()
                .put("say", new StringMessage("Hello You"))
                .put("to", new StringMessage("bar")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("Hello You"))
                .put("from", new StringMessage("foo"));

        assert bar.received(message);
        assert !baz.received(message);
        assert !foo.received(message);
    }

    @Test
    public void listenToTopic() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        lobby(new CompositeMessage()
                .put("enter", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        avatar(foo, new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("About that"))
                .put("on", new StringMessage("a topic"))
                .put("from", new StringMessage("bar"));

        assert foo.received(message);
        assert !bar.received(message);
        assert !baz.received(message);
    }

    @Test
    public void leftTopic() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        avatar(foo, new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        avatar(foo, new CompositeMessage()
                .put(0, new StringMessage("leave")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 2;
    }

    @Test
    public void nobodyCares() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 1;
        assert bar.received.size() == 1;
    }

    @Test
    public void ignoreTopic() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        avatar(foo, new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        avatar(foo, new CompositeMessage()
                .put("ignore", new StringMessage("a topic")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 1;
    }

    @Test
    public void catchUp() {
        lobby(new CompositeMessage()
                .put("enter", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        lobby(new CompositeMessage()
                .put("enter", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        avatar(foo, new CompositeMessage()
                .put("join", new StringMessage("bla")));
        dish.remove(foo.address);

        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("one")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("two")));
        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("topic"))
                .put("on", new StringMessage("bla")));

        assert foo.received.size() == 1;

        foo.address = dish.add(foo);

        avatar(bar, new CompositeMessage()
                .put("say", new StringMessage("three")));
        assert foo.received.size() == 1;

        avatar(foo, new CompositeMessage()
                .put("connect", foo.addressMessage()));

        assert foo.received.size() == 5;
        assert foo.received.get(1).equals(new CompositeMessage()
                .put("message", new StringMessage("one"))
                .put("from", new StringMessage("bar")));
        assert foo.received.get(2).equals(new CompositeMessage()
                .put("message", new StringMessage("two"))
                .put("from", new StringMessage("bar")));
        assert foo.received.get(3).equals(new CompositeMessage()
                .put("message", new StringMessage("topic"))
                .put("on", new StringMessage("bla"))
                .put("from", new StringMessage("bar")));
        assert foo.received.get(4).equals(new CompositeMessage()
                .put("message", new StringMessage("three"))
                .put("from", new StringMessage("bar")));
    }

    private void lobby(Message message) {
        send(lobby, message);
    }

    private void avatar(FakeZell zell, Message message) {
        send(zell.received.get(0).read("avatar").asAddress(), message);
    }

    private void send(Address receiver, Message message) {
        dish.send(receiver, message);
    }
}
