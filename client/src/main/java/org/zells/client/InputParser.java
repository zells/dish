package org.zells.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class InputParser {

    private String receiver;
    private Message message;
    private List<Message> receivedMessages;
    private Map<String, Address> aliases;

    InputParser(String input, List<Message> receivedMessages, Map<String, Address> aliases) throws Exception {
        this.receivedMessages = receivedMessages;
        this.aliases = aliases;

        int firstSpace = input.indexOf(" ");
        if (firstSpace < 0) {
            receiver = input;
            message = new NullMessage();
            return;
        }

        receiver = input.substring(0, firstSpace);

        if (receiver.startsWith("#")) {
            receiver = resolveReference(receiver.substring(1)).asString();
        }

        String rawMessage = input.substring(firstSpace + 1).trim();
        if (rawMessage.startsWith("!")) {
            message = parseJsonMessage(rawMessage.substring(1));
        } else {
            message = parseShortSyntaxMessage(rawMessage);
        }
    }

    String getReceiver() {
        return receiver;
    }

    Message getMessage() {
        return message;
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

    private Message parseShortSyntaxMessage(String input) {
        CompositeMessage message = new CompositeMessage();

        Map<String, CompositeMessage> collections = new HashMap<String, CompositeMessage>();
        boolean quoted = false;
        boolean escaped = false;
        boolean reference = false;

        int index = 0;
        String key = "0";
        StringBuilder bag = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (!escaped && !quoted && c == ' ') {
                message.put(key, combinedMessage(collections, bag, key, reference));
                bag = new StringBuilder();
                index++;
                key = Integer.toString(index);
                reference = false;
            } else if (!escaped && !quoted && c == ':') {
                key = bag.toString();
                bag = new StringBuilder();
                index--;
            } else if (!escaped && !quoted && c == '#') {
                reference = true;
            } else if (!escaped && c == '"') {
                quoted = !quoted;
            } else if (c == '\\') {
                escaped = !escaped;
            } else {
                escaped = false;
                bag.append(c);
            }
        }
        message.put(key, combinedMessage(collections, bag, key, reference));

        return message;
    }

    private Message combinedMessage(Map<String, CompositeMessage> collections, StringBuilder bag, String key, boolean reference) {
        if (!collections.containsKey(key)) {
            collections.put(key, new CompositeMessage());
        }
        CompositeMessage composite = collections.get(key);
        int lastKey = composite.keys().size();

        Message value;
        if (reference) {
            value = resolveReference(bag.toString());
        } else {
            value = parseShortSyntaxPart(bag.toString());
        }

        composite.put(lastKey, value);

        if (lastKey == 0) {
            return composite.read(0);
        } else {
            return composite;
        }
    }

    private Message resolveReference(String reference) {
        String[] parts = reference.split("\\.");
        int id = Integer.valueOf(parts[0]);

        if (receivedMessages.size() <= id) {
            throw new RuntimeException("Invalid reference: " + reference);
        }

        Message message = new CompositeMessage().put(id, receivedMessages.get(id));
        for (String part : parts) {
            message = message.read(part);
        }

        return message;
    }

    private Message parseShortSyntaxPart(String part) {
        if (part.matches("^\\d+$")) {
            return new IntegerMessage(Integer.valueOf(part));
        } else if (part.equals("yes")) {
            return new BooleanMessage(true);
        } else if (part.equals("no")) {
            return new BooleanMessage(false);
        } else if (part.startsWith("0x")) {
            return BinaryMessage.fromString(part);
        } else if (part.startsWith("@")) {
            return new StringMessage(aliases.get(part.substring(1)).toString());
        } else {
            return new StringMessage(part);
        }
    }
}
