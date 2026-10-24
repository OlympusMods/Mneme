package com.github.olympus.mneme.core.macro;

import com.github.olympus.mneme.core.JsonFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MacroStore {
    private final JsonFile<Macro> file;
    private final List<Macro> macros;
    private boolean dirty;

    public MacroStore(Path path) {
        this.file = JsonFile.of(path, Macro.class);
        this.macros = file.read();
    }

    public List<Macro> all() {
        return new ArrayList<>(macros);
    }

    public Macro add() {
        Macro macro = new Macro();
        macros.add(macro);
        dirty = true;
        return macro;
    }

    public void remove(Macro macro) {
        macros.remove(macro);
        dirty = true;
    }

    public void markDirty() {
        dirty = true;
    }

    public void flush() {
        if (dirty) {
            file.write(macros);
            dirty = false;
        }
    }
}
