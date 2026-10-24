package com.github.olympus.mneme.core.portal;

import com.github.olympus.mneme.core.JsonFile;
import com.github.olympus.mneme.core.portal.PortalMath.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PortalStore {
    private final JsonFile<PortalPin> file;
    private final List<PortalPin> pins;

    public PortalStore(Path path) {
        this.file = JsonFile.of(path, PortalPin.class);
        this.pins = file.read();
    }

    public List<PortalPin> all() {
        return new ArrayList<>(pins);
    }

    public PortalPin add(String name, Point overworld, Point nether) {
        PortalPin pin = new PortalPin();
        pin.place(name, overworld, nether);
        pins.add(pin);
        file.write(pins);
        return pin;
    }

    public void show(PortalPin pin, boolean value) {
        pin.show(value);
        file.write(pins);
    }

    public void remove(PortalPin pin) {
        pins.remove(pin);
        file.write(pins);
    }
}
