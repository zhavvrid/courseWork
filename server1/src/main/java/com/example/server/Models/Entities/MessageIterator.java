package com.example.server.Models.Entities;
import com.example.server.Interfaces.Iterator;

import java.util.List;

public class MessageIterator implements Iterator<Message> {
    private final List<Message> messages;
    private int position;

    public MessageIterator(List<Message> messages) {
        this.messages = messages;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < messages.size();
    }

    @Override
    public Message next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements.");
        }
        return messages.get(position++);
    }
}

