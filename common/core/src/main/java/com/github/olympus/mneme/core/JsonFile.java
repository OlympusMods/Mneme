package com.github.olympus.mneme.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonFile<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("mneme");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;
    private final Type type;

    private JsonFile(Path file, Type type) {
        this.file = file;
        this.type = type;
    }

    public static <T> JsonFile<T> of(Path file, Class<T> element) {
        return new JsonFile<>(file, TypeToken.getParameterized(List.class, element).getType());
    }

    public List<T> read() {
        List<T> items = new ArrayList<>();
        if (!Files.isRegularFile(file)) {
            return items;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<T> read = GSON.fromJson(reader, type);
            if (read != null) {
                items.addAll(read);
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not read {}: {}", file, exception.getMessage());
        }
        return items;
    }

    public void write(List<T> items) {
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(items, type, writer);
            }
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}: {}", file, exception.getMessage());
        }
    }
}
