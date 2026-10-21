package com.github.olympus.mneme.core.note;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Note {
    private String id = UUID.randomUUID().toString();
    private String title = "";
    private NoteKind kind = NoteKind.TEXT;
    private String text = "";
    private int color;
    private NoteMaterial material = NoteMaterial.PAPER;
    private List<CheckItem> items = new ArrayList<>();
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

    public int color() {
        return color;
    }

    public void setColor(int value) {
        color = value;
        touch();
    }

    public NoteMaterial material() {
        return material == null ? NoteMaterial.PAPER : material;
    }

    public void setMaterial(NoteMaterial value) {
        material = value;
        touch();
    }

    public List<CheckItem> items() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public CheckItem addItem(int index) {
        CheckItem item = new CheckItem();
        items().add(Math.min(index, items().size()), item);
        touch();
        return item;
    }

    public void removeItem(CheckItem item) {
        items().remove(item);
        touch();
    }

    public void changed() {
        touch();
    }

    public int doneCount() {
        int count = 0;
        for (CheckItem item : items()) {
            if (item.done()) {
                count++;
            }
        }
        return count;
    }

    public long updated() {
        return updated;
    }

    private void touch() {
        updated = System.currentTimeMillis();
    }
}
