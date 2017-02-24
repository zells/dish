package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;
import org.zells.dish.delivery.Address;

public class AliasAddressesTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();

        FakeDish.nextAddress = "fade";
        new Client(dish, user);
    }

    @Test
    public void defaultAlias() {
        user.hear("client hello");
        assert dish.sent.get(0).getKey().equals(Address.fromString("fade"));
    }

    @Test
    public void addAlias() {
        user.hear("client alias use:foo for:0xdada");
        user.hear("foo bar");

        assert user.told.contains("Set alias [foo] for [0xdada]");
        assert dish.sent.get(1).getKey().equals(Address.fromString("dada"));
    }

    @Test
    public void ignoreSpaces() {
        user.hear("client alias use:\"foo bar\" for:0xdada");

        assert user.told.contains("Set alias [foobar] for [0xdada]");
    }

    @Test
    public void overwriteAlias() {
        user.hear("client alias use:foo for:dada");
        user.hear("client alias use:foo for:fade");
        user.hear("foo bar");

        assert dish.sent.get(2).getKey().equals(Address.fromString("fade"));
    }

    @Test
    public void removeAlias() {
        user.hear("client alias use:da for:dada");
        user.hear("client alias remove:da");
        user.hear("da hello");

        assert user.told.contains("Removed alias [da]");
        assert dish.sent.get(2).getKey().equals(Address.fromString("da"));
    }

    @Test
    public void removeNonExistingAlias() {
        user.hear("client alias remove:da");
        assert user.told.contains("Error: No such alias: da");
    }

    @Test
    public void listAliases() {
        user.hear("client alias use:foo for:dada");
        user.hear("client alias use:bar for:fade");
        user.hear("client alias");

        assert user.told.contains("Aliases:\n" +
                "bar: 0xfade\n" +
                "foo: 0xdada\n" +
                "client: 0xfade\n");
    }
}
