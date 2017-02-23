package org.zells.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

class InputParser {

    private Address address;
    private Message message;

    InputParser(String input) throws Exception {
        int firstSpace = input.indexOf(" ");
        if (firstSpace < 0) {
            address = Address.fromString(input);
            message = new NullMessage();
            return;
        }

        address = Address.fromString(input.substring(0, firstSpace));

        String rawMessage = input.substring(firstSpace + 1).trim();
        if (rawMessage.startsWith("!")) {
            message = parseJsonMessage(rawMessage.substring(1));
        }
    }

    private Message parseJsonMessage(String jsonString) throws IOException {
        return parseJsonNode(new ObjectMapper().readTree(jsonString));
    }

    private Message parseJsonNode(JsonNode json) throws IOException {
        if (json.isNull()) {
            return new NullMessage();
        } else if (json.isTextual() && json.asText().startsWith("0x")) {
            return BinaryMessage.fromString(json.asText());
        } else if (json.isTextual()) {
            return new StringMessage(json.asText());
        } else if (json.isInt()) {
            return new IntegerMessage(json.asInt());
        } else if (json.isBoolean()) {
            return new BooleanMessage(json.asBoolean());
        } else if (json.isObject()) {
            CompositeMessage message = new CompositeMessage();
            for (Iterator<Map.Entry<String, JsonNode>> it = json.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                message.put(entry.getKey(), parseJsonNode(entry.getValue()));
            }
            return message;
        } else if (json.isArray()) {
            CompositeMessage message = new CompositeMessage();
            for (int i = 0; i < json.size(); i++) {
                message.put(i, parseJsonNode(json.get(i)));
            }
            return message;
        }

        throw new IOException("Could not parse: " + json);
    }

    Address getAddress() {
        return address;
    }

    Message getMessage() {
        return message;
    }
}
