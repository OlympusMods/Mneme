package com.github.olympus.mneme.core.note;

import java.util.UUID;

public final class CheckItem {
    private final String id = UUID.randomUUID().toString();
    private String text = "";
    private boolean done;

    public String id() {
        return id;
    }

    public String text() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public boolean done() {
        return done;
    }

    public void setDone(boolean value) {
        done = value;
    }
}
