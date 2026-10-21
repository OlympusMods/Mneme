package com.github.olympus.mneme.core.macro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public final class Script {
    public static final int MAX_REPEAT = 1000;

    private final List<Step> steps;
    private final List<String> errors;

    private Script(List<Step> steps, List<String> errors) {
        this.steps = steps;
        this.errors = errors;
    }

    public List<Step> steps() {
        return steps;
    }

    public List<String> errors() {
        return errors;
    }

    public static Script parse(String source) {
        List<String> errors = new ArrayList<>();
        List<Step> root = new ArrayList<>();
        Deque<List<Step>> bodies = new ArrayDeque<>();
        Deque<Integer> counts = new ArrayDeque<>();
        List<Step> current = root;
        String[] lines = source.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int space = line.indexOf(' ');
            String word = (space < 0 ? line : line.substring(0, space)).toLowerCase(Locale.ROOT);
            String rest = space < 0 ? "" : line.substring(space + 1).strip();
            int number = i + 1;
            switch (word) {
                case "say" -> add(current, rest.isEmpty() ? null : new Step.Say(rest), errors, number, "say needs a message");
                case "command", "cmd" -> add(current, rest.isEmpty() ? null : new Step.Command(stripSlash(rest)),
                        errors, number, "command needs a command");
                case "echo" -> add(current, rest.isEmpty() ? null : new Step.Echo(rest), errors, number,
                        "echo needs a message");
                case "toast" -> add(current, rest.isEmpty() ? null : new Step.Toast(rest), errors, number,
                        "toast needs a message");
                case "sound" -> {
                    Step sound = sound(rest);
                    add(current, sound, errors, number, "sound needs a name like bell or minecraft:block.anvil.land");
                }
                case "wait" -> {
                    int ticks = ticks(rest);
                    add(current, ticks < 0 ? null : new Step.Wait(ticks), errors, number,
                            "wait needs a time like 20t, 2s or 1.5");
                }
                case "repeat" -> {
                    int times = count(rest);
                    if (times < 1 || times > MAX_REPEAT) {
                        errors.add("Line " + number + ": repeat needs a number from 1 to " + MAX_REPEAT);
                        times = 1;
                    }
                    bodies.push(current);
                    counts.push(times);
                    current = new ArrayList<>();
                }
                case "end" -> {
                    if (bodies.isEmpty()) {
                        errors.add("Line " + number + ": end without repeat");
                    } else {
                        List<Step> body = current;
                        current = bodies.pop();
                        current.add(new Step.Repeat(counts.pop(), body));
                    }
                }
                default -> errors.add("Line " + number + ": unknown command " + word);
            }
        }
        if (!bodies.isEmpty()) {
            errors.add("A repeat is missing its end");
            while (!bodies.isEmpty()) {
                List<Step> body = current;
                current = bodies.pop();
                current.add(new Step.Repeat(counts.pop(), body));
            }
        }
        return new Script(root, errors);
    }

    private static void add(List<Step> into, Step step, List<String> errors, int line, String problem) {
        if (step == null) {
            errors.add("Line " + line + ": " + problem);
        } else {
            into.add(step);
        }
    }

    private static Step sound(String text) {
        String[] parts = text.strip().split("\\s+");
        if (parts[0].isEmpty()) {
            return null;
        }
        try {
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1f;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1f;
            return new Step.Sound(parts[0], Math.max(0f, Math.min(4f, volume)), Math.max(0.5f, Math.min(2f, pitch)));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String stripSlash(String text) {
        return text.startsWith("/") ? text.substring(1) : text;
    }

    private static int count(String text) {
        try {
            return Integer.parseInt(text.strip());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static int ticks(String text) {
        String value = text.strip().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return -1;
        }
        try {
            if (value.endsWith("t")) {
                return Math.max(0, Integer.parseInt(value.substring(0, value.length() - 1).strip()));
            }
            if (value.endsWith("s")) {
                value = value.substring(0, value.length() - 1).strip();
            }
            return Math.max(0, Math.round(Float.parseFloat(value) * 20f));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
