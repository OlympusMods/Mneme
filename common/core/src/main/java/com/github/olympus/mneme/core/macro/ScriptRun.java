package com.github.olympus.mneme.core.macro;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.UnaryOperator;

public final class ScriptRun {
    public interface Actions {
        void say(String text);

        void command(String text);

        void echo(String text);

        void toast(String text);
    }

    private static final int STEPS_PER_TICK = 200;

    private static final class Frame {
        final List<Step> steps;
        int index;
        int loopsLeft;

        Frame(List<Step> steps, int loops) {
            this.steps = steps;
            this.loopsLeft = loops;
        }
    }

    private final Deque<Frame> frames = new ArrayDeque<>();
    private int waiting;

    public ScriptRun(Script script) {
        frames.push(new Frame(script.steps(), 1));
    }

    public boolean done() {
        return frames.isEmpty();
    }

    public void tick(Actions actions, UnaryOperator<String> expand) {
        if (waiting > 0) {
            waiting--;
            return;
        }
        int budget = STEPS_PER_TICK;
        while (!frames.isEmpty() && waiting == 0 && budget-- > 0) {
            Frame frame = frames.peek();
            if (frame.index >= frame.steps.size()) {
                if (frame.loopsLeft > 1) {
                    frame.loopsLeft--;
                    frame.index = 0;
                } else {
                    frames.pop();
                }
                continue;
            }
            Step step = frame.steps.get(frame.index++);
            switch (step) {
                case Step.Say say -> actions.say(expand.apply(say.text()));
                case Step.Command command -> actions.command(expand.apply(command.text()));
                case Step.Echo echo -> actions.echo(expand.apply(echo.text()));
                case Step.Toast toast -> actions.toast(expand.apply(toast.text()));
                case Step.Wait wait -> waiting = wait.ticks();
                case Step.Repeat repeat -> frames.push(new Frame(repeat.body(), repeat.times()));
            }
        }
        if (budget < 0 && !frames.isEmpty()) {
            frames.clear();
        }
    }
}
