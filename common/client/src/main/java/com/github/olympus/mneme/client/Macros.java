package com.github.olympus.mneme.client;

import com.github.olympus.mneme.core.macro.ChatSource;
import com.github.olympus.mneme.core.macro.Macro;
import com.github.olympus.mneme.core.macro.Script;
import com.github.olympus.mneme.core.macro.ScriptRun;
import com.github.olympus.mneme.core.macro.Trigger;
import com.github.olympus.proteus.client.ui.ToastOverlay;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class Macros {
    private record Active(ScriptRun run, Map<String, String> variables) {
    }

    private static final Map<String, Active> RUNNING = new HashMap<>();
    private static final Set<String> HELD = new HashSet<>();
    private static final Map<String, Integer> COUNTERS = new HashMap<>();
    private static final int JOIN_DELAY = 40;

    private static boolean echoing;
    private static boolean acting;
    private static boolean inWorld;
    private static boolean dead;
    private static int joinCountdown;

    private Macros() {
    }

    public static void tick(Minecraft minecraft) {
        if (minecraft.player != null) {
            pollKeys(minecraft);
            pollEvents(minecraft);
        } else {
            inWorld = false;
            dead = false;
            joinCountdown = 0;
            COUNTERS.clear();
        }
        Iterator<Map.Entry<String, Active>> iterator = RUNNING.entrySet().iterator();
        ScriptRun.Actions actions = new GameActions(minecraft);
        while (iterator.hasNext()) {
            Active active = iterator.next().getValue();
            active.run().tick(actions, text -> GameInfo.expand(minecraft, variables(text, active.variables())));
            if (active.run().done()) {
                iterator.remove();
            }
        }
    }

    public static void start(Macro macro) {
        start(macro, Map.of());
    }

    public static void chat(String message, ChatSource source) {
        if (echoing || acting || Minecraft.getInstance().player == null) {
            return;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        for (Macro macro : MnemeData.macros().all()) {
            String pattern = macro.pattern().toLowerCase(Locale.ROOT);
            if (macro.trigger() == Trigger.CHAT && macro.enabled() && macro.listensTo(source)
                    && !pattern.isEmpty() && lower.contains(pattern)) {
                start(macro, Map.of("message", message, "source", source.name().toLowerCase(Locale.ROOT)));
            }
        }
    }

    private static void start(Macro macro, Map<String, String> variables) {
        if (RUNNING.containsKey(macro.id())) {
            return;
        }
        Script script = Script.parse(macro.script());
        if (!script.steps().isEmpty()) {
            RUNNING.put(macro.id(), new Active(new ScriptRun(script), variables));
        }
    }

    private static String variables(String text, Map<String, String> variables) {
        String result = text;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public static boolean running(Macro macro) {
        return RUNNING.containsKey(macro.id());
    }

    private static void pollEvents(Minecraft minecraft) {
        if (!inWorld) {
            inWorld = true;
            joinCountdown = JOIN_DELAY;
        }
        if (joinCountdown > 0 && --joinCountdown == 0) {
            startAll(Trigger.JOIN);
        }
        boolean dying = minecraft.player.isDeadOrDying();
        if (dying && !dead) {
            startAll(Trigger.DEATH);
        }
        dead = dying;
        for (Macro macro : MnemeData.macros().all()) {
            if (macro.trigger() != Trigger.INTERVAL || !macro.enabled()) {
                continue;
            }
            int ticks = COUNTERS.merge(macro.id(), 1, Integer::sum);
            if (ticks >= macro.interval() * 20) {
                COUNTERS.put(macro.id(), 0);
                start(macro);
            }
        }
    }

    private static void startAll(Trigger trigger) {
        for (Macro macro : MnemeData.macros().all()) {
            if (macro.trigger() == trigger && macro.enabled()) {
                start(macro);
            }
        }
    }

    private static void pollKeys(Minecraft minecraft) {
        long window = GLFW.glfwGetCurrentContext();
        for (Macro macro : MnemeData.macros().all()) {
            if (macro.trigger() != Trigger.KEY || macro.key() <= 0 || !macro.enabled()) {
                continue;
            }
            boolean down = GLFW.glfwGetKey(window, macro.key()) == GLFW.GLFW_PRESS;
            boolean was = HELD.contains(macro.id());
            if (down && !was && Compat.screen(minecraft) == null) {
                start(macro);
            }
            if (down) {
                HELD.add(macro.id());
            } else {
                HELD.remove(macro.id());
            }
        }
    }

    private record GameActions(Minecraft minecraft) implements ScriptRun.Actions {
        @Override
        public void say(String text) {
            if (minecraft.player != null) {
                acting = true;
                try {
                    minecraft.player.connection.sendChat(text);
                } finally {
                    acting = false;
                }
            }
        }

        @Override
        public void command(String text) {
            if (minecraft.player != null) {
                acting = true;
                try {
                    minecraft.player.connection.sendCommand(text);
                } finally {
                    acting = false;
                }
            }
        }

        @Override
        public void sound(String name, float volume, float pitch) {
            Compat.playSound(minecraft, SoundNames.resolve(name), volume, pitch);
        }

        @Override
        public void echo(String text) {
            if (minecraft.player != null) {
                echoing = true;
                try {
                    Compat.echo(minecraft, text);
                } finally {
                    echoing = false;
                }
            }
        }

        @Override
        public void toast(String text) {
            ToastOverlay.show(Component.translatable("mneme.screen.title").getString(), text, "writable_book");
        }
    }
}
