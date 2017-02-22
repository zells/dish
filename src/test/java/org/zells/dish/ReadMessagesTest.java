package org.zells.dish;

import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;

import java.util.Arrays;

public class ReadMessagesTest {

    @Test
    public void nullMessage() {
        Message m = new NullMessage();

        assert m.isNull();
        assert !m.isTrue();
        assert m.asString() == null;
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBinary(), new byte[0]);
        assert m.keys().isEmpty();
    }

    @Test
    public void trueBooleanMessage() {
        Message m = new BooleanMessage(true);

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("true");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBinary(), new byte[]{1});
        assert m.keys().isEmpty();
    }

    @Test
    public void falseBooleanMessage() {
        Message m = new BooleanMessage(false);

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBinary(), new byte[]{0});
        assert m.keys().isEmpty();
    }

    @Test
    public void emptyStringMessage() {
        Message m = new StringMessage("");

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBinary(), new byte[0]);
        assert m.keys().isEmpty();
    }

    @Test
    public void stringMessage() {
        Message m = new StringMessage("foo");

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("foo");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBinary(), "foo".getBytes());
        assert m.keys().isEmpty();
    }

    @Test
    public void integerMessage() {
        Message m = new IntegerMessage(42);

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("42");
        assert m.asInteger() == 42;
        assert Arrays.equals(m.asBinary(), new byte[]{0, 0, 0, 42});
        assert m.keys().isEmpty();
    }

    @Test
    public void zeroIntegerMessage() {
        Message m = new IntegerMessage(0);

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("0");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBinary(), new byte[]{0, 0, 0, 0});
        assert m.keys().isEmpty();
    }

    @Test
    public void binaryMessage() {
        Message m = new BinaryMessage("foo".getBytes());

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("foo");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBinary(), "foo".getBytes());
        assert m.keys().isEmpty();
    }

    @Test
    public void emptyBinaryMessage() {
        Message m = new BinaryMessage(new byte[0]);

        assert m.isNull();
        assert !m.isTrue();
        assert m.asString() == null;
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBinary(), new byte[0]);
        assert m.keys().isEmpty();
    }

    @Test
    public void addressMessage() {
        Message m = new AddressMessage(Address.fromString("aa"));

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("aa");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBinary(), Address.fromString("aa").toBytes());
        assert m.keys().isEmpty();
        assert m.asAddress().equals(Address.fromString("aa"));
    }

    @Test
    public void compositeMessage() {
        Message m = (new CompositeMessage())
                .put("one", new StringMessage("uno"))
                .put("and", (new CompositeMessage())
                        .put("two", new IntegerMessage(2)));

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("{and={two=2}, one=uno}");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBinary(), "{and={two=2}, one=uno}".getBytes());

        assert m.keys().contains("one");
        assert m.keys().contains("and");
        assert m.read("one").asString().equals("uno");
        assert m.read("and").read("two").asInteger() == 2;
    }
}
