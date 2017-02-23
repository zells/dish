package org.zells.client.tests.fakes;

import org.zells.client.User;

import java.util.ArrayList;
import java.util.List;

public class FakeUser extends User {

    public List<String> told = new ArrayList<String>();

    public void tell(String output) {
        told.add(output);
    }
}
