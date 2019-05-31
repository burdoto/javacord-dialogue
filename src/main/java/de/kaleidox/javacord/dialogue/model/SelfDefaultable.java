package de.kaleidox.javacord.dialogue.model;

import java.util.Optional;

public interface SelfDefaultable<Self, T> {
    Self withDefaultValue(T value);

    Optional<T> getDefaultValue();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    default Self removeDefaultValue() {
        withDefaultValue(null);
        return (Self) this;
    }
}
