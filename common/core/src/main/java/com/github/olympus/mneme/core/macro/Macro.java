package com.github.olympus.mneme.core.macro;

import java.util.UUID;

public final class Macro {
    private String id = UUID.randomUUID().toString();
    private String name = "";
    private boolean enabled = true;
    private Trigger trigger = Trigger.KEY;
    private int key;
    private int interval = 60;
    private String pattern = "";
    private String script = "";

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public Trigger trigger() {
        return trigger == null ? Trigger.KEY : trigger;
    }

    public void setTrigger(Trigger value) {
        trigger = value;
    }

    public int key() {
        return key;
    }

    public void setKey(int value) {
        key = value;
    }

    public int interval() {
        return Math.max(1, interval);
    }

    public void setInterval(int seconds) {
        interval = Math.max(1, seconds);
    }

    public String pattern() {
        return pattern == null ? "" : pattern;
    }

    public void setPattern(String value) {
        pattern = value;
    }

    public String script() {
        return script == null ? "" : script;
    }

    public void setScript(String value) {
        script = value;
    }
}
