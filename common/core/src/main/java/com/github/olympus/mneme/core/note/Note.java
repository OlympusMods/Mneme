package com.github.olympus.mneme.core.note;

import java.util.UUID;

public final class Note {
    private String id = UUID.randomUUID().toString();
    private String title = "";
    private NoteKind kind = NoteKind.TEXT;
    private String text = "";
    private long updated = System.currentTimeMillis();

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public void setTitle(String value) {
        title = value;
        touch();
    }

    public NoteKind kind() {
        return kind;
    }

    public void setKind(NoteKind value) {
        kind = value;
        touch();
    }

    public String text() {
        return text;
    }

    public void setText(String value) {
        text = value;
        touch();
    }

    public long updated() {
        return updated;
    }

    private void touch() {
        updated = System.currentTimeMillis();
    }
}
