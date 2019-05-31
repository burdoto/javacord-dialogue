package de.kaleidox.javacord.dialogue.input.option.model;

import org.javacord.api.entity.Nameable;

public class Option<T> implements Nameable {
    private final String name;
    private final String description;
    private final T value;

    public Option(String name, String description, T value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }
}
