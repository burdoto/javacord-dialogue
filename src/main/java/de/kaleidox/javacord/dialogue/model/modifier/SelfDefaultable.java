package de.kaleidox.javacord.dialogue.model.modifier;

import java.util.Optional;

import org.jetbrains.annotations.Contract;

public interface SelfDefaultable<Self, T> {
    @Contract(value = "_ -> this", mutates = "this")
    Self withDefaultValue(T value);

    Optional<T> getDefaultValue();

    @SuppressWarnings("ConstantConditions")
    @Contract(value = "-> this", mutates = "this")
    default Self removeDefaultValue() {
        return withDefaultValue(null);
    }
}
