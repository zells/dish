package org.zells.samples.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;
import org.zells.samples.LobbyZell;
import org.zells.samples.tests.fakes.FakeZell;

import java.util.HashMap;
import java.util.Map;

public class LobbyTest {

    private Dish dish;
    private Address lobby;
    private FakeZell foo;
    private FakeZell bar;
    private FakeZell baz;
    private Map<FakeZell, Address> avatars = new HashMap<FakeZell, Address>();

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

    private FakeZell addZell(final Dish dish) {
        FakeZell zell = new FakeZell() {
            @Override
            public void receive(Message message) {
                super.receive(message);

                if (message.read("avatar") instanceof AddressMessage) {
                    Address avatar = message.read("avatar").asAddress();
                    dish.send(avatar, new CompositeMessage().put("subscribe", new AddressMessage(this.address)));
                    avatars.put(this, avatar);
                }
            }
        };
        zell.address = dish.add(zell);
        return zell;
    }

    @Test
    public void enter() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));

        assert foo.received.get(0).read(0).equals(new StringMessage("Hello, foo"));
        assert foo.received.get(0).read("avatar") instanceof AddressMessage;
    }

    @Test
    public void introducePeople() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", baz.addressMessage()));

        assert baz.received(new CompositeMessage()
                .put("people", new CompositeMessage()
                        .put(0, new StringMessage("bar"))
                        .put(1, new StringMessage("foo"))));
    }

    @Test
    public void alterEgo() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", baz.addressMessage()));

        assert baz.received(new CompositeMessage()
                .put("people", new CompositeMessage()
                        .put(0, new StringMessage("bar"))
                        .put(1, new StringMessage("foo"))));
    }

    @Test
    public void nameTaken() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("foo")));

        assert bar.received(new CompositeMessage()
                .put("error", new StringMessage("There is already somebody here with that name.")));
    }

    @Test
    public void leave() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put(0, new StringMessage("leave")));

        assert receivedMessage(foo, new StringMessage("Good-bye"));

        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("hello"))
                .put("from", bar.addressMessage()));

        assert bar.received(new CompositeMessage()
                .put("people", new CompositeMessage()));
    }

    @Test
    public void talkToAll() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("say", new StringMessage("Hello World")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("from", new StringMessage("foo"));

        assert !receivedMessage(foo, message);
        assert receivedMessage(bar, message);
        assert receivedMessage(baz, message);
    }

    @Test
    public void leftLobby() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put(0, new StringMessage("leave")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("say", new StringMessage("Hello World")));

        assert bar.received.size() == 2;
    }

    @Test
    public void talkToSomebody() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("say", new StringMessage("Hello You"))
                .put("to", new StringMessage("bar")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("Hello You"))
                .put("from", new StringMessage("foo"));

        assert !receivedMessage(foo, message);
        assert receivedMessage(bar, message);
        assert !receivedMessage(baz, message);
    }

    @Test
    public void listenToTopic() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", baz.addressMessage())
                .put("as", new StringMessage("baz")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        CompositeMessage message = new CompositeMessage()
                .put("message", new StringMessage("About that"))
                .put("on", new StringMessage("a topic"))
                .put("from", new StringMessage("bar"));

        assert receivedMessage(foo, message);
        assert !receivedMessage(bar, message);
        assert !receivedMessage(baz, message);
    }

    @Test
    public void leftTopic() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put(0, new StringMessage("leave")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 2;
    }

    @Test
    public void nobodyCares() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 1;
        assert bar.received.size() == 1;
    }

    @Test
    public void ignoreTopic() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("join", new StringMessage("a topic")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("ignore", new StringMessage("a topic")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("About that"))
                .put("on", new StringMessage("a topic")));

        assert foo.received.size() == 1;
    }

    @Test
    public void catchUp() {
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", foo.addressMessage())
                .put("as", new StringMessage("foo")));
        dish.send(lobby, new CompositeMessage()
                .put(0, new StringMessage("enter"))
                .put("from", bar.addressMessage())
                .put("as", new StringMessage("bar")));
        dish.send(avatars.get(foo), new CompositeMessage()
                .put("join", new StringMessage("bla")));

        dish.remove(foo.address);

        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("one")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("two")));
        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("topic"))
                .put("on", new StringMessage("bla")));

        assert foo.received.size() == 1;

        foo.address = dish.add(foo);

        dish.send(avatars.get(bar), new CompositeMessage()
                .put("say", new StringMessage("three")));
        assert foo.received.size() == 1;

        dish.send(avatars.get(foo), new CompositeMessage()
                .put("subscribe", foo.addressMessage()));

        assert foo.received.size() == 5;
        assert receivedMessage(foo, new CompositeMessage()
                .put("message", new StringMessage("one"))
                .put("from", new StringMessage("bar")));
        assert receivedMessage(foo, new CompositeMessage()
                .put("message", new StringMessage("two"))
                .put("from", new StringMessage("bar")));
        assert receivedMessage(foo, new CompositeMessage()
                .put("message", new StringMessage("topic"))
                .put("on", new StringMessage("bla"))
                .put("from", new StringMessage("bar")));
        assert receivedMessage(foo, new CompositeMessage()
                .put("message", new StringMessage("three"))
                .put("from", new StringMessage("bar")));
    }

    private boolean receivedMessage(FakeZell zell, Message message) {
        for (Message m : zell.received) {
            if (m.read("message").equals(message)) {
                return true;
            }
        }
        return false;
    }
}
