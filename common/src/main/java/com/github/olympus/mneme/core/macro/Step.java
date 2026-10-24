package com.github.olympus.mneme.core.macro;

import java.util.List;

public sealed interface Step {
    record Say(String text) implements Step {
    }

    record Command(String text) implements Step {
    }

    record Echo(String text) implements Step {
    }

    record Toast(String text) implements Step {
    }

    record Sound(String name, float volume, float pitch) implements Step {
    }

    record Wait(int ticks) implements Step {
    }

    record Repeat(int times, List<Step> body) implements Step {
    }
}
