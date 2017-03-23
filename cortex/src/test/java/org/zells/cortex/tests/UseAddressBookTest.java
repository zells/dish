package org.zells.cortex.tests;

import org.junit.Test;
import org.zells.cortex.synapses.communicator.Communicator;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UseAddressBookTest extends BaseTest {

    @Test
    public void addEntry() {
        targetBook();
        send("entries at:foo put:0xdada");

        assert book.has("foo");
        assert book.get("foo").equals(Address.fromString("dada"));
    }

    @Test
    public void ignoreSpaces() {
        targetBook();
        send("entries at:\"foo bar\" put:0xdada");

        assert book.has("foobar");
        assert book.get("foobar").equals(Address.fromString("dada"));
    }

    @Test
    public void replaceEntry() {
        targetBook();
        send("entries at:foo put:0xdada");
        send("entries at:foo put:0xfade");

        assert book.get("foo").equals(Address.fromString("fade"));
    }

    @Test
    public void removeEntry() {
        targetBook();
        send("entries at:foo put:0xdada");
        send("entries remove:foo");

        assert !book.has("foo");
    }

    @Test
    public void listEntries() {
        targetBook();

        final List<Message> got = new ArrayList<Message>();
        dish.put(Address.fromString("baba"), new Zell() {
            @Override
            public void receive(Message message) {
                got.add(message);
            }
        });

        send("entries at:foo put:0xdada");
        send("entries at:bar put:0xfade");
        send("entries tell:0xbaba");

        assert got.equals(Collections.singletonList(new CompositeMessage()
                .put("foo", new AddressMessage(Address.fromString("dada")))
                .put("bar", new AddressMessage(Address.fromString("fade")))));
    }

    @Test
    public void observeChanges() {
        targetBook();

        final List<Message> got = new ArrayList<Message>();
        dish.put(Address.fromString("baba"), new Zell() {
            @Override
            public void receive(Message message) {
                got.add(message);
            }
        });

        send("observers put:0xbaba");
        assert got.isEmpty();

        send("entries at:foo put:0xdada");
        assert got.get(0).equals(new CompositeMessage(new StringMessage("observer"))
                .put("stateChanged", new CompositeMessage()
                        .put("added", new CompositeMessage()
                                .put("foo", new AddressMessage(Address.fromString("dada"))))));

        send("entries at:foo put:0xfade");
        assert got.get(1).equals(new CompositeMessage(new StringMessage("observer"))
                .put("stateChanged", new CompositeMessage()
                        .put("replaced", new CompositeMessage()
                                .put("foo", new AddressMessage(Address.fromString("fade"))))));

        send("entries remove:foo");
        assert got.get(2).equals(new CompositeMessage(new StringMessage("observer"))
                .put("stateChanged", new CompositeMessage()
                        .put("removed", new CompositeMessage()
                                .put("foo", new AddressMessage(Address.fromString("fade"))))));

        send("entries remove:foo");
        assert got.size() == 3;
    }

    @Test
    public void useNameAsReceiver() {
        final List<Message> got = new ArrayList<Message>();
        book.put("foo", dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                got.add(message);
            }
        }));
        send("foo < Hello");

        assert got.equals(Collections.singletonList(new CompositeMessage(new StringMessage("Hello"))));
    }

    @Test
    public void useNameInMessage() {
        book.put("foo", Address.fromString("dada"));

        send("@0xbaba");
        assert received.get(0).read(0).equals(new AddressMessage(Address.fromString("baba")));

        send("!\"@0xbaba\"");
        assert received.get(1).equals(new AddressMessage(Address.fromString("baba")));

        send("@foo");
        assert received.get(2).read(0).equals(new AddressMessage(Address.fromString("dada")));

        send("!\"@foo\"");
        assert received.get(3).equals(new AddressMessage(Address.fromString("dada")));
    }

    private void targetBook() {
        target = dish.add(book);
        communicator = new Communicator(target, dish, book);
    }
}
